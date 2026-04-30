import random

qs = []
scores = {}

def addq(txt, opts, ans):
    qs.append({"t": txt, "o": opts, "a": ans})

def runq(player):
    random.shuffle(qs)
    s = 0
    for q in qs:
        print(q["t"])
        for i, o in enumerate(q["o"]):
            print(f"{i+1}. {o}")
        ans = input("Answer: ")
        if ans == q["a"]:
            s += 1
    if player not in scores:
        scores[player] = []
    scores[player].append(s)

def topscore(player):
    return max(scores[player])

def avg(player):
    lst = scores[player]
    return sum(lst) / len(lst)
