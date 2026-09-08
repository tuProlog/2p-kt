package it.unibo.tuprolog.examples.solve;

import it.unibo.tuprolog.core.*;
import it.unibo.tuprolog.core.Integer;
import it.unibo.tuprolog.solve.Solution;
import it.unibo.tuprolog.solve.Solver;
import it.unibo.tuprolog.solve.flags.TrackVariables;
import it.unibo.tuprolog.solve.primitive.Solve;
import it.unibo.tuprolog.solve.primitive.UnaryPredicate;
import it.unibo.tuprolog.theory.Theory;
import it.unibo.tuprolog.theory.parsing.ClausesParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Java counterpart of {@code it.unibo.tuprolog.examples.solve.ThermostatAgentKt}: a reactive
 * agent whose control loop is a Prolog program, while its {@code get_temp/1} sensor and
 * {@code push/1} actuator primitives are plain Java objects, showing how the {@code :solve}
 * public API reads from Java (e.g. going through {@code request.getUnificator().mgu(...)} rather
 * than the {@code mgu} extension available in Kotlin).
 *
 * <p>The agent runs as a {@link Thread}: its control logic is loaded from the bundled
 * {@code thermostat.pl} resource (templated with the cold/hot thresholds passed to the
 * constructor) and solved once via {@code it.unibo.tuprolog.solve.Solver}, printing whether the
 * target temperature range was reached, the logic program failed, or an exception was raised.
 */
@SuppressWarnings({"rawtypes", "FieldMayBeFinal", "unchecked", "NullableProblems", "ConstantConditions"})
public class ThermostatAgent extends Thread {
    private int coldThreshold;
    private int hotThreshold;
    private int temperature;

    /**
     * @param name the thread name.
     * @param coldThreshold the temperature at or below which the agent pushes hot air.
     * @param hotThreshold the temperature at or above which the agent pushes cold air.
     * @param temperature the starting simulated temperature.
     */
    public ThermostatAgent(String name, int coldThreshold, int hotThreshold, int temperature) {
        super(name);
        this.coldThreshold = coldThreshold;
        this.hotThreshold = hotThreshold;
        this.temperature = temperature;
    }

    /** @return the agent's current simulated temperature. */
    public int getTemperature() {
        return temperature;
    }

    private UnaryPredicate getTemp = new UnaryPredicate.Functional("get_temp") {
        protected Substitution computeOneSubstitution(Solve.Request request, Term first) {
            ensuringArgumentIsVariable(request, 0);
            return request.getUnificator().mgu(first, Integer.of(temperature));
        }
    };

    private UnaryPredicate push = new UnaryPredicate.Predicative("push") {
        @Override
        protected boolean compute(Solve.Request request, Term first) {
            ensuringAllArgumentsAreInstantiated(request);
            ensuringArgumentIsAtom(request, 0);
            String firstValue = first.castToAtom().getValue();
            if ("hot".equals(firstValue)) {
                temperature++;
            } else if ("cold".equals(firstValue)) {
                temperature--;
            } else {
                return false;
            }
            return true;
        }
    };

    private String agentProgram() throws IOException {
        // opens the file in examples/src/main/resources/it/unibo/tuprolog/examples/solve
        InputStream thermostat = this.getClass().getResourceAsStream("thermostat.pl");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(thermostat))) {
            return reader.lines().collect(Collectors.joining("\n"))
                    .replace("__COLD_THRESHOLD__", coldThreshold + "")
                    .replace("__HOT_THRESHOLD__", hotThreshold + "");
        }
    }

    private ClausesParser prologParser = ClausesParser.withDefaultOperators();

    /**
     * Parses the templated {@code thermostat.pl} program, builds a solver registering
     * {@code get_temp/1} and {@code push/1} under the {@code libs.agency.thermostat} library
     * alias (with variable tracking enabled so bound variables remain visible), and solves the
     * {@code start/0} goal once, printing the outcome to standard output.
     */
    @Override
    public void run() {
        try {
            Theory theory = prologParser.parseTheory(agentProgram());
            System.out.println(theory);
            Solver solver = Solver.prolog().newBuilder()
                    .staticKb(theory)
                    .flag(TrackVariables.INSTANCE, TrackVariables.ON)
                    .library("libs.agency.thermostat", getTemp, push)
                    .build();
            Solution solution = solver.solveOnce(Atom.of("start"));
            if (solution.isYes()) {
                System.out.println("Reached target temperature: " + temperature);
            } else if (solution.isNo()) {
                System.out.println("Failure in logic program");
            } else {
                System.out.println("Error in logic program:");
                for (Struct entry : solution.getException().getLogicStackTrace()) {
                    System.out.println("\tin " + entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Entry point spawning a {@link ThermostatAgent} with a cold threshold of 20, a hot threshold
     * of 24, and a starting temperature of 30, then waiting for it to reach a stable temperature
     * before the JVM exits.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting for the
     *     agent thread to terminate.
     */
    public static void main(String[] args) throws InterruptedException {
        ThermostatAgent agent = new ThermostatAgent("thermostat", 20, 24, 30);
        agent.start();
        agent.join();
    }
}
