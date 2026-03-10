import { createClient } from '@supabase/supabase-js';

const supabaseUrl = import.meta.env.VITE_SUPABASE_URL;
const supabaseAnonKey = import.meta.env.VITE_SUPABASE_SUPABASE_ANON_KEY;

if (!supabaseUrl || !supabaseAnonKey) {
  throw new Error('Missing Supabase environment variables');
}

export const supabase = createClient(supabaseUrl, supabaseAnonKey);

export type PriorityLevel = {
  id: number;
  name: 'LOW' | 'MEDIUM' | 'HIGH' | 'EXTREME';
  level: number;
};

export type Patient = {
  id: string;
  name: string;
  age: number;
  priority_level_id: number;
  arrival_order: number;
  created_at: string;
};

export type QueueEntry = {
  id: number;
  patient_id: string;
  priority_score: number;
  arrival_order: number;
  added_at: string;
};

export type QueueHistoryEntry = {
  id: number;
  patient_id: string;
  action: 'ADDED' | 'CALLED' | 'REMOVED';
  action_timestamp: string;
};

export type PatientWithPriority = Patient & {
  priority_levels: PriorityLevel;
  queue: QueueEntry[];
};
