import pytest
from quiz_engine_refactored import QuizEngine

def sample_questions(engine):
    engine.add_question("2+2?", ["3", "4", "5"], 2)
    engine.add_question("Capital of France?", ["Paris", "Rome"], 1)

def test_add_question():
    engine = QuizEngine(); engine.add_question("Q", ["A", "B"], 1); assert len(engine.questions) == 1

def test_calculate_score_all_correct():
    engine = QuizEngine(); sample_questions(engine); assert engine.calculate_score(engine.questions, [2,1]) == 2

def test_calculate_score_partially_correct():
    engine = QuizEngine(); sample_questions(engine); assert engine.calculate_score(engine.questions, [1,1]) == 1

def test_invalid_submitted_answer():
    engine = QuizEngine(); sample_questions(engine)
    with pytest.raises(ValueError): engine.calculate_score(engine.questions, [5,1])

def test_missing_player_top_score():
    with pytest.raises(ValueError): QuizEngine().get_top_score("x")

def test_missing_player_average_score():
    with pytest.raises(ValueError): QuizEngine().get_average_score("x")

def test_recording_scores():
    engine = QuizEngine(); engine.record_score("p", 2); assert engine.get_player_scores("p") == [2]

def test_top_score_calculation():
    engine = QuizEngine(); engine.record_score("p",1); engine.record_score("p",3); assert engine.get_top_score("p") == 3

def test_average_score_calculation():
    engine = QuizEngine(); engine.record_score("p",2); engine.record_score("p",4); assert engine.get_average_score("p") == 3

def test_player_score_list_retrieval():
    engine = QuizEngine(); assert engine.get_player_scores("none") == []
