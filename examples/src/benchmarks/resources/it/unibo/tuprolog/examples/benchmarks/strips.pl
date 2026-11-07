% STRIPS planning over the blocks world (#1075), adapted from
% https://github.com/unibo-fc-isi-ise/code-strips (Strips.pl, BlockWorld.pl) without logging.
% States, goals and action effects are sets, whose representation (list, tuple or block) is up to the
% strips-sets-*.pl file loaded alongside: set_of/2, set_in/2, subseteq/2, set_difference/3, set_union/3.

max_depth(8).

% strips(+InitList, +GoalList, -Plan): Plan reaches GoalList from InitList, by iterative deepening
strips(InitList, GoalList, Plan) :-
    set_of(InitList, Init),
    set_of(GoalList, Goal),
    strips(Init, Goal, 1, 1, Plan).

strips(_, _, _, Max, _) :- max_depth(Max), !, fail.
strips(Init, Goal, N, Max, Plan) :-
    strips_impl(Init, Goal, [], N, Max, _, RevPlan),
    N =< Max,
    reverse(RevPlan, Plan).
strips(Init, Goal, _, Max, Plan) :-
    NewMax is Max + 1,
    strips(Init, Goal, 0, NewMax, Plan).

strips_impl(State, Goal, Plan, N, Max, State, Plan) :-
    N =< Max,
    subseteq(Goal, State).
strips_impl(State, Goal, Plan, N, Max, FinalState, FinalPlan) :-
    N =< Max,
    set_in(SubGoal, Goal),
    \+ set_in(SubGoal, State),
    action(Action, Pre, Add, _),
    set_in(SubGoal, Add),
    N1 is N + 1,
    strips_impl(State, Pre, Plan, N1, Max, TmpState, TmpPlan),
    apply_action(TmpState, Action, NewState),
    strips_impl(NewState, Goal, [Action | TmpPlan], N1, Max, FinalState, FinalPlan).

apply_action(State, Action, NewState) :-
    action(Action, Pre, Add, Del),
    set_difference(State, Del, TmpState),
    subseteq(Pre, State),
    set_union(Add, TmpState, NewState).

% action(Action, Preconditions, Additions, Deletions), as sets
action(Action, Pre, Add, Del) :-
    action_def(Action, PreList, AddList, DelList),
    set_of(PreList, Pre),
    set_of(AddList, Add),
    set_of(DelList, Del).

action_def(stack(X, Y), [clear(Y), holding(X)], [on(X, Y), clear(X), handempty], [clear(Y), holding(X)]).
action_def(unstack(X, Y), [on(X, Y), clear(X), handempty], [clear(Y), holding(X)], [on(X, Y), clear(X), handempty]).
action_def(pick(X), [ontable(X), clear(X), handempty], [holding(X)], [ontable(X), clear(X), handempty]).
action_def(put(X), [holding(X)], [ontable(X), clear(X), handempty], [holding(X)]).

% the Sussman anomaly: c is on a, which must end up on b, on c
sussman(Plan) :-
    strips(
        [on(c, a), ontable(a), ontable(b), clear(c), clear(b), handempty],
        [on(a, b), on(b, c)],
        Plan
    ).

% the first plan for the Sussman anomaly, checked against the expected one
sussman_checked :-
    once(sussman(Plan)),
    Plan == [
        unstack(c, a), stack(c, b), pick(a), put(a), unstack(c, b), put(c), pick(a), stack(a, b),
        unstack(a, b), put(a), pick(b), stack(b, c), pick(a), stack(a, b)
    ].
