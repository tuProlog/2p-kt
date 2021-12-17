countSolution(Animals, Count) :-
    findall(_, zoo(Animals, _), Bag),
    count(Bag, 0, Count).

count([],Res,Res).
count([_|T],C,Res) :-
    C1 is C+1,
    count(T,C1,Res).

zoo(Animals, Zoo) :- zoo(Animals, Animals, Zoo).
zoo(All, [A|Animals], [Cage|Zoo]) :-
    cage(A, All, Cage),
    remTwo(Cage, All, NewAll),
    remTwo(Cage, Animals, NewAnimals),
    zoo(NewAll, NewAnimals, Zoo).
zoo(_, [A], [cage(A)]).
zoo(_, [],[]).

cage(A, [A2|_],cage(A,A2)) :- compatible(A, A2).
cage(A, [_|Animals],Cage) :- cage(A,Animals,Cage).

remTwo(cage(A,B), All, Out) :-
    rem(A, All, Tmp),
    rem(B, Tmp, Out).
rem(A, [A|T], T).
rem(_, [], []).
rem(A, [B|T], [B|Out]) :- A\=B, rem(A, T, Out).

% wolf -> sheep, lion, tiger, cat, monkey
compatible(wolf, elephant).
compatible(wolf, mouse).
compatible(wolf, dog).
compatible(wolf, cow).
compatible(wolf, snake).
compatible(wolf, hippo).
compatible(wolf, parrot).
compatible(wolf, lizard).
% lion -> sheep, wolf, tiger, hippo, cow, dog
compatible(lion, elephant).
compatible(lion, mouse).
compatible(lion, cat).
compatible(lion, snake).
compatible(lion, monkey).
compatible(lion, parrot).
compatible(lion, lizard).
% tiger -> sheep, wolf, lion, dog, cow
compatible(tiger, elephant).
compatible(tiger, mouse).
compatible(tiger, cat).
compatible(tiger, snake).
compatible(tiger, monkey).
compatible(tiger, hippo).
compatible(tiger, parrot).
compatible(tiger, lizard).
% sheep -> wolf, lion, tiger, snake
compatible(sheep, elephant).
compatible(sheep, mouse).
compatible(sheep, cat).
compatible(sheep, dog).
compatible(sheep, cow).
compatible(sheep, monkey).
compatible(sheep, hippo).
compatible(sheep, parrot).
compatible(sheep, lizard).
% elephant -> mouse, hippo
compatible(elephant, wolf).
compatible(elephant, lion).
compatible(elephant, tiger).
compatible(elephant, sheep).
compatible(elephant, cat).
compatible(elephant, dog).
compatible(elephant, cow).
compatible(elephant, snake).
compatible(elephant, monkey).
compatible(elephant, parrot).
compatible(elephant, lizard).
% mouse -> cat, parrot, snake, elephant
compatible(mouse, wolf).
compatible(mouse, lion).
compatible(mouse, tiger).
compatible(mouse, sheep).
compatible(mouse, dog).
compatible(mouse, cow).
compatible(mouse, monkey).
compatible(mouse, hippo).
compatible(mouse, lizard).
% cat -> dog, mouse, parrot, snake, lizard, wolf
compatible(cat, lion).
compatible(cat, tiger).
compatible(cat, sheep).
compatible(cat, elephant).
compatible(cat, cow).
compatible(cat, monkey).
compatible(cat, hippo).
% dog -> cat, tiger, snake, lion
compatible(dog, wolf).
compatible(dog, sheep).
compatible(dog, elephant).
compatible(dog, mouse).
compatible(dog, cow).
compatible(dog, monkey).
compatible(dog, hippo).
compatible(dog, parrot).
compatible(dog, lizard).
% cow -> lion, tiger
compatible(cow, wolf).
compatible(cow, sheep).
compatible(cow, elephant).
compatible(cow, mouse).
compatible(cow, cat).
compatible(cow, dog).
compatible(cow, snake).
compatible(cow, monkey).
compatible(cow, hippo).
compatible(cow, parrot).
compatible(cow, lizard).
% snake -> mouse, cat, dog, monkey, parrot, lizard, sheep
compatible(snake, wolf).
compatible(snake, lion).
compatible(snake, tiger).
compatible(snake, elephant).
compatible(snake, cow).
compatible(snake, hippo).
% monkey -> snake, wolf
compatible(monkey, lion).
compatible(monkey, tiger).
compatible(monkey, sheep).
compatible(monkey, elephant).
compatible(monkey, mouse).
compatible(monkey, cat).
compatible(monkey, dog).
compatible(monkey, cow).
compatible(monkey, hippo).
compatible(monkey, parrot).
compatible(monkey, lizard).
% hippo -> elephant, lion
compatible(hippo, wolf).
compatible(hippo, tiger).
compatible(hippo, sheep).
compatible(hippo, mouse).
compatible(hippo, cat).
compatible(hippo, dog).
compatible(hippo, cow).
compatible(hippo, snake).
compatible(hippo, monkey).
compatible(hippo, parrot).
compatible(hippo, lizard).
% parrot -> cat, snake, mouse, lizard
compatible(parrot, wolf).
compatible(parrot, lion).
compatible(parrot, tiger).
compatible(parrot, sheep).
compatible(parrot, elephant).
compatible(parrot, dog).
compatible(parrot, cow).
compatible(parrot, monkey).
compatible(parrot, hippo).
% lizard -> snake, parrot, cat
compatible(lizard, wolf).
compatible(lizard, lion).
compatible(lizard, tiger).
compatible(lizard, sheep).
compatible(lizard, elephant).
compatible(lizard, mouse).
compatible(lizard, dog).
compatible(lizard, cow).
compatible(lizard, monkey).
compatible(lizard, hippo).