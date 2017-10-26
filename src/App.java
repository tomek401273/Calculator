import java.util.*;

// Wprowadzamy dodatkowe uogólnienie, dla większej manipulacji listą termów.
interface Calc {

    int priority();

    List<Calc> calculcate(List<Calc> termList, int currentIndex);

}

abstract class BracketCalc implements Calc {


    @Override
    public List<Calc> calculcate(List<Calc> termList, int currentIndex) {
        int endIdx = 0;

        for (int i = currentIndex; i < termList.size(); i++) {
            if (termList.get(i) instanceof BeginSquarebrackets) {
                endIdx = i;
            }

            if (termList.get(i) instanceof BeginBracketCalc) {
                endIdx = i;
            }

        }
        System.out.println("endIdx po pentli for " + endIdx);

        int backBr = 0;
        for (int j = endIdx; j < termList.size(); j++) {
            if (termList.get(j) instanceof EndBracketCalc) {
                backBr = j;
                break;
            }
            if (termList.get(j) instanceof EndSquareBrackets) {
                backBr = j;
                break;
            }
        }

        List<Calc> calcs = termList.subList(endIdx + 1, backBr);

        ArrayList<Calc> newCalcs = new ArrayList<>();

        if (endIdx != 0) {
            newCalcs.addAll(termList.subList(0, endIdx));
        }
        newCalcs.add(new Evaluator(calcs).calculate());
        newCalcs.addAll(termList.subList(backBr - 1, termList.size()));

        return newCalcs;
    }
}

class BeginBracketCalc extends BracketCalc {


    @Override
    public int priority() {
        return 1000;
    }


}

class EndBracketCalc extends BracketCalc {

    @Override
    public int priority() {
        return 1000;
    }
}

class BeginSquarebrackets extends BracketCalc {

    @Override
    public int priority() {
        return 1001;
    }


}

class EndSquareBrackets extends BracketCalc {

    @Override
    public int priority() {
        return 1001;
    }
}

abstract class Term implements Calc {

    @Override
    public List<Calc> calculcate(List<Calc> termList, int index) {
        Calc term = termList.get(index);
        Term calculatedTerm = ((Term) term).calculcate(termList.get(index - 1), termList.get(index + 1));

        termList.remove(index - 1);
        termList.remove(index - 1);
        termList.remove(index - 1);
        termList.add(index - 1, calculatedTerm);
        return termList;
    }

    abstract public Term calculcate(Calc term1, Calc term2);
}

class NumberTerm extends Term {

    private double number;

    public NumberTerm(double term) {
        this.number = term;
    }

    public double getNumber() {
        return number;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public Term calculcate(Calc term1, Calc term2) {
        return null;
    }

    public double getSum() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Result your operation: " + getSum();
    }

}

class Div extends Term {

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public Term calculcate(Calc term1, Calc term2) {
        NumberTerm termFirst = (NumberTerm) term1;
        NumberTerm termSecond = (NumberTerm) term2;

        System.out.println("first number" + termFirst.getNumber());
        System.out.println("secend number" + termSecond.getNumber());
        NumberTerm div = new NumberTerm(termFirst.getNumber() / termSecond.getNumber());
        System.out.println("Div: " + div.getSum());
        return div;
    }
}

class MultiTerm extends Term {

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public Term calculcate(Calc term1, Calc term2) {
        NumberTerm termFirst = (NumberTerm) term1;
        NumberTerm termSecond = (NumberTerm) term2;

        System.out.println("first number" + termFirst.getNumber());
        System.out.println("secend number" + termSecond.getNumber());
        NumberTerm mult = new NumberTerm(termFirst.getNumber() * termSecond.getNumber());
        System.out.println("Mult: " + mult.getSum());
        return mult;
    }
}

class SubtractionTerm extends Term {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Term calculcate(Calc term1, Calc term2) {
        NumberTerm termFirst = (NumberTerm) term1;
        NumberTerm termSecond = (NumberTerm) term2;
        System.out.println("first number" + termFirst.getNumber());
        System.out.println("secend number" + termSecond.getNumber());
        NumberTerm diff = new NumberTerm(termFirst.getNumber() - termSecond.getNumber());
        System.out.println("Diff: " + diff.getSum());
        return diff;
    }

}

class AdditionTerm extends Term {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public Term calculcate(Calc term1, Calc term2) {
        NumberTerm termFirst = (NumberTerm) term1;
        NumberTerm termSecond = (NumberTerm) term2;
        System.out.println("first number" + termFirst.getNumber());
        System.out.println("secend number" + termSecond.getNumber());
        NumberTerm sum = new NumberTerm(termFirst.getNumber() + termSecond.getNumber());
        System.out.println("SUM: " + sum.getSum());
        return sum;
    }
}

class CalcFactory {

    private Map<String, Calc> register;

    public CalcFactory() {

        register = new HashMap<>();
        register.put("[", new BeginSquarebrackets());
        register.put("]", new EndSquareBrackets());
        register.put("(", new BeginBracketCalc());
        register.put(")", new EndBracketCalc());
        register.put("+", new AdditionTerm());
        register.put("-", new SubtractionTerm());
        register.put("*", new MultiTerm());
        register.put("/", new Div());

    }

    public Calc createCalc(String term) {
        if (register.containsKey(term)) {
            return register.get(term);
        }
        return new NumberTerm(Double.valueOf(term));
    }
}

class Evaluator {

    private String toCalculate;

    private List<Calc> calcs;

    Evaluator(String toCalculate) {
        this.toCalculate = toCalculate;
    }

    Evaluator(List<Calc> calcList) {
        this.calcs = calcList;
    }

    public Calc calculate() {
        if (calcs == null) {
            List<String> split = Arrays.asList(toCalculate.split(" "));
            calcs = new LinkedList<>();

            CalcFactory calcFactory = new CalcFactory();
            for (String term : split) {
                calcs.add(calcFactory.createCalc(term));
            }
        }

        while (calcs.size() > 2) {

            Calc maxTerm = calcs.get(0);
            int maxTermprio = calcs.get(0).priority();
            int index = 0;

            for (int i = 0; i < calcs.size(); i++) {
                if (calcs.get(i).priority() > maxTermprio) {
                    maxTermprio = calcs.get(i).priority();
                    maxTerm = calcs.get(i);
                    index = i;
                }
            }

            calcs = maxTerm.calculcate(calcs, index);
        }

        return calcs.get(0);
    }

    public static void main(String[] args) {
        Calc calculate = new Evaluator("[ ( 2 + 1 ) * ( ( 2 + 1 ) / [ 1 + 2 ] ) ] + ( 2 * 2 ) + 1 + [ 2 * 2 ]").calculate();
        System.out.println(calculate);
    }
}
