:- dynamic(done/1).

warm_range(__COLD_THRESHOLD__, __HOT_THRESHOLD__).
done(no).

start :-
    repeat,
    sleep(100 /* ms */), % just to slow down the execution
    warm_range(Min, Max),
    keep_temperature(Min, Max),
    done(yes).

keep_temperature(Min, Max) :-
    check_temperature(T),
    handle_temperature(T, Min, Max).

check_temperature(T) :-
    get_temp(T),
    write("Temperature is "), write(T), write('.'), nl.

handle_temperature(T, Min, _) :- T =< Min, !,
    push(hot),
    write("Pushing hot air."), nl.

handle_temperature(T, _, Max) :- T >= Max, !,
    push(cold),
    write("Pushing cold air."), nl.

handle_temperature(_, _, _) :-
    retract(done(_)), !,
    assert(done(yes)),
    write("I'm done."), nl.
