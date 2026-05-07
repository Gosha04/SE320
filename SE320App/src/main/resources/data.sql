-- Seed: Cognitive Distortions
MERGE INTO cognitive_distortions (id, name, description) KEY(id) VALUES
('all-or-nothing', 'All-or-Nothing Thinking', 'Seeing situations in black-and-white categories with no middle ground.'),
('catastrophizing', 'Catastrophizing', 'Expecting the worst possible outcome and treating it as likely.'),
('mind-reading', 'Mind Reading', 'Assuming you know what others are thinking without evidence.'),
('fortune-telling', 'Fortune Telling', 'Predicting negative outcomes as if they are facts.'),
('overgeneralization', 'Overgeneralization', 'Drawing broad, negative conclusions from a single event.'),
('labeling', 'Labeling', 'Assigning fixed, global labels to yourself or others based on specific behaviors.'),
('emotional-reasoning', 'Emotional Reasoning', 'Believing something is true because it feels true.'),
('should-statements', 'Should Statements', 'Using rigid rules about how you or others should behave.');

MERGE INTO cognitive_distortion_examples (distortion_id, example) KEY(distortion_id, example) VALUES
('all-or-nothing', 'If I make one mistake, I am a total failure.'),
('catastrophizing', 'If I do poorly on this test, my life is ruined.'),
('mind-reading', 'They did not reply quickly, so they must be upset with me.'),
('fortune-telling', 'I just know this interview will go badly.'),
('overgeneralization', 'This one bad day means nothing ever works out for me.'),
('labeling', 'I forgot that task, I am so incompetent.'),
('emotional-reasoning', 'I feel anxious, so this must be dangerous.'),
('should-statements', 'I should always be productive, no matter what.');

-- Seed: Session Modules
MERGE INTO session_modules (id, name) KEY(id) VALUES
('11111111-1111-1111-1111-111111111111', 'Foundations'),
('22222222-2222-2222-2222-222222222222', 'Thought Work'),
('33333333-3333-3333-3333-333333333333', 'Behavior Change');

-- Seed: Sample CBT Sessions
MERGE INTO cbt_sessions (id, module_id, title, description, duration_minutes, order_index, session_id) KEY(id) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '11111111-1111-1111-1111-111111111111', 'Understanding Thoughts, Feelings, and Behaviors', 'Introduces the CBT model and the connection between thoughts, emotions, and actions.', 20, 1, 1001),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '22222222-2222-2222-2222-222222222222', 'Thought Record Practice', 'Guided practice identifying automatic thoughts and building balanced alternatives.', 25, 2, 1002),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', '33333333-3333-3333-3333-333333333333', 'Behavioral Activation Planning', 'Create a small, achievable activity plan to improve mood through action.', 20, 3, 1003);

MERGE INTO cbt_session_objectives (session_id, objective) KEY(session_id, objective) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Understand the CBT triangle.'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Recognize how thoughts influence mood.'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'Identify automatic thoughts in a real scenario.'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'Write a balanced alternative thought.'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'Select one values-based activity.'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'Define a realistic completion plan.');

MERGE INTO cbt_session_modalities (session_id, modality) KEY(session_id, modality) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'COGNITIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'MINDFULNESS'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'COGNITIVE'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'JOURNALING'),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'BEHAVIORAL');

-- Seed: Coping Strategies
MERGE INTO coping_strategies (id, name, category, description) KEY(id) VALUES
('44444444-4444-4444-4444-444444444441', 'Box Breathing', 'Breathing', 'Inhale for 4 seconds, hold for 4, exhale for 4, hold for 4. Repeat for 2-5 minutes.'),
('44444444-4444-4444-4444-444444444442', '5-4-3-2-1 Grounding', 'Grounding', 'Name 5 things you see, 4 feel, 3 hear, 2 smell, and 1 taste to reconnect with the present.'),
('44444444-4444-4444-4444-444444444443', 'Thought Reframing', 'Cognitive', 'Identify a negative automatic thought and replace it with a balanced, evidence-based alternative.'),
('44444444-4444-4444-4444-444444444444', 'Behavioral Activation Micro-Step', 'Behavioral', 'Choose one small, meaningful action you can complete in under 10 minutes.'),
('44444444-4444-4444-4444-444444444445', 'Progressive Muscle Relaxation', 'Relaxation', 'Tense and release muscle groups from head to toe while breathing slowly.'),
('44444444-4444-4444-4444-444444444446', 'Compassionate Self-Talk', 'Self-Compassion', 'Respond to self-criticism as you would to a close friend with warmth and realism.');
