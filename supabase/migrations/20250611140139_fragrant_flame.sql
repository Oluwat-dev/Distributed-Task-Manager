/*
  # Create users table and related structures
  
  1. New Tables
    - `users`
      - `id` (uuid, primary key)
      - `email` (text, unique, not null)
      - `first_name` (text, not null)
      - `last_name` (text, not null) 
      - `password` (text, not null)
      - `enabled` (boolean, default true)
      - `account_non_expired` (boolean, default true)
      - `account_non_locked` (boolean, default true)
      - `credentials_non_expired` (boolean, default true)
      - `last_login_at` (timestamp)
      - `created_at` (timestamp, not null)
      - `updated_at` (timestamp, not null)
      - `version` (bigint, for optimistic locking)
    
    - `user_roles`
      - `user_id` (uuid, foreign key to users.id)
      - `role` (text, enum values)
  
  2. Security
    - Add unique constraint on email
    - Add indexes for performance
    - Add foreign key constraint for user_roles
*/

CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email text UNIQUE NOT NULL,
    first_name text NOT NULL,
    last_name text NOT NULL,
    password text NOT NULL,
    enabled boolean NOT NULL DEFAULT true,
    account_non_expired boolean NOT NULL DEFAULT true,
    account_non_locked boolean NOT NULL DEFAULT true,
    credentials_non_expired boolean NOT NULL DEFAULT true,
    last_login_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    version bigint NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id uuid NOT NULL,
    role text NOT NULL,
    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_enabled ON users(enabled);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON user_roles(role);

-- Create composite index for user roles lookup
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id_role ON user_roles(user_id, role);