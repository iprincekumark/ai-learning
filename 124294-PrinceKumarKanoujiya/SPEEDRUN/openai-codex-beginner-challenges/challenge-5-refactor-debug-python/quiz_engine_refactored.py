class QuizEngine:
    def __init__(self):
        self.questions = []
        self.scores_by_player = {}

    def add_question(self, text, options, correct_answer_number):
        if not text or not isinstance(options, list) or len(options) < 2:
            raise ValueError("Question text and at least two options are required")
        if not isinstance(correct_answer_number, int) or correct_answer_number < 1 or correct_answer_number > len(options):
            raise ValueError("Correct answer number must be within option range")
        self.questions.append({"text": text, "options": options, "correct": correct_answer_number})

    def calculate_score(self, questions, submitted_answers):
        if len(questions) != len(submitted_answers):
            raise ValueError("Submitted answers count must match question count")
        score = 0
        for q, submitted in zip(questions, submitted_answers):
            if not isinstance(submitted, int) or submitted < 1 or submitted > len(q["options"]):
                raise ValueError("Each submitted answer must be a valid option number")
            if submitted == q["correct"]:
                score += 1
        return score

    def record_score(self, player, score):
        if not player:
            raise ValueError("Player name is required")
        self.scores_by_player.setdefault(player, []).append(score)

    def get_top_score(self, player):
        scores = self.get_player_scores(player)
        if not scores:
            raise ValueError("No scores found for player")
        return max(scores)

    def get_average_score(self, player):
        scores = self.get_player_scores(player)
        if not scores:
            raise ValueError("No scores found for player")
        return sum(scores) / len(scores)

    def get_player_scores(self, player):
        return list(self.scores_by_player.get(player, []))
