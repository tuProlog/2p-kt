package it.unibo.tuprolog.ui.gui.plp

import it.unibo.tuprolog.ui.gui.template.TheoryTemplate

/** Well-known ProbLog tutorial examples, ready to load into a PLP-capable page. */
object PlpTheoryTemplates {
    val BAYESIAN_ALARM =
        TheoryTemplate(
            id = "bayesian-alarm",
            displayName = "Bayesian alarm network",
            description = "Burglary/earthquake trigger an alarm, each with its own probability.",
            source =
                """
                % https://dtai.cs.kuleuven.be/problog/tutorial/basic/02_bayes.html

                0.7::burglary.
                0.2::earthquake.
                0.9::p_alarm1.
                0.8::p_alarm2.
                0.1::p_alarm3.

                alarm :- burglary, earthquake, p_alarm1.
                alarm :- burglary, \+earthquake, p_alarm2.
                alarm :- \+burglary, earthquake, p_alarm3.

                % Try:  burglary.
                % Try:  alarm.
                """.trimIndent(),
        )

    val PROBABILISTIC_GRAPH =
        TheoryTemplate(
            id = "probabilistic-graph",
            displayName = "Probabilistic graph reachability",
            description = "Edges that exist with some probability; queries ask for path probability.",
            source =
                """
                % https://dtai.cs.kuleuven.be/problog/tutorial/basic/04_pgraph.html

                0.6::edge(1,2).
                0.1::edge(1,3).
                0.4::edge(2,5).
                0.3::edge(2,6).
                0.3::edge(3,4).
                0.8::edge(4,5).
                0.2::edge(5,6).

                path(X,Y) :- edge(X,Y).
                path(X,Y) :- edge(X,Z), Y \== Z, path(Z,Y).

                % Try:  path(1,5).
                % Try:  path(1,6).
                """.trimIndent(),
        )

    val ROLLING_DICE =
        TheoryTemplate(
            id = "rolling-dice",
            displayName = "Rolling dice",
            description = "Annotated disjunctions modelling two differently-weighted six-sided dice.",
            source =
                """
                % https://dtai.cs.kuleuven.be/problog/tutorial/basic/03_dice.html

                1/6::one1; 1/6::two1; 1/6::three1; 1/6::four1; 1/6::five1; 1/6::six1.
                0.15::one2; 0.15::two2; 0.15::three2; 0.15::four2; 0.15::five2; 0.25::six2.

                twoSix :- six1, six2.
                someSix :- six1.
                someSix :- six2.

                % Try:  six1.
                % Try:  someSix.
                """.trimIndent(),
        )

    val ALL: List<TheoryTemplate> = listOf(BAYESIAN_ALARM, PROBABILISTIC_GRAPH, ROLLING_DICE)
}
