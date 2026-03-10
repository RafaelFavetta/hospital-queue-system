import { supabase, Patient, PatientWithPriority } from '../lib/supabase';

export const queueService = {
  async addPatient(name: string, age: number, priorityLevelId: number) {
    const { data: maxArrivalData } = await supabase
      .from('patients')
      .select('arrival_order')
      .order('arrival_order', { ascending: false })
      .limit(1)
      .maybeSingle();

    const nextArrivalOrder = (maxArrivalData?.arrival_order || 0) + 1;

    // Generate ULID-like ID (simplified version)
    const id = `${Date.now().toString(36)}${Math.random().toString(36).substring(2)}`.toUpperCase();

    // Calculate priority score
    const priorityLevel = priorityLevelId;
    const elderlyBonus = age >= 60 ? 5 : 0;
    const priorityScore = priorityLevel * 10 + elderlyBonus;

    // Insert patient
    const { data: patient, error: patientError } = await supabase
      .from('patients')
      .insert({
        id,
        name,
        age,
        priority_level_id: priorityLevelId,
        arrival_order: nextArrivalOrder,
      })
      .select()
      .single();

    if (patientError) throw patientError;

    // Add to queue
    const { error: queueError } = await supabase
      .from('queue')
      .insert({
        patient_id: id,
        priority_score: priorityScore,
        arrival_order: nextArrivalOrder,
      });

    if (queueError) throw queueError;

    // Log to history
    await supabase
      .from('queue_history')
      .insert({
        patient_id: id,
        action: 'ADDED',
      });

    return patient;
  },

  async getQueue() {
    const { data, error } = await supabase
      .from('queue')
      .select(`
        *,
        patients!inner (
          *,
          priority_levels!inner (*)
        )
      `)
      .order('priority_score', { ascending: false })
      .order('arrival_order', { ascending: true });

    if (error) throw error;
    return data;
  },

  async callNextPatient() {
    const { data: queueData, error: queueError } = await supabase
      .from('queue')
      .select(`
        *,
        patients!inner (
          *,
          priority_levels!inner (*)
        )
      `)
      .order('priority_score', { ascending: false })
      .order('arrival_order', { ascending: true })
      .limit(1)
      .maybeSingle();

    if (queueError) throw queueError;
    if (!queueData) return null;

    const patientId = queueData.patient_id;

    // Remove from queue
    const { error: deleteError } = await supabase
      .from('queue')
      .delete()
      .eq('patient_id', patientId);

    if (deleteError) throw deleteError;

    // Log to history
    await supabase
      .from('queue_history')
      .insert({
        patient_id: patientId,
        action: 'CALLED',
      });

    return queueData;
  },

  async getQueueSize() {
    const { count, error } = await supabase
      .from('queue')
      .select('*', { count: 'exact', head: true });

    if (error) throw error;
    return count || 0;
  },

  subscribeToQueue(callback: () => void) {
    const channel = supabase
      .channel('queue_changes')
      .on(
        'postgres_changes',
        { event: '*', schema: 'public', table: 'queue' },
        callback
      )
      .subscribe();

    return () => {
      supabase.removeChannel(channel);
    };
  },
};
