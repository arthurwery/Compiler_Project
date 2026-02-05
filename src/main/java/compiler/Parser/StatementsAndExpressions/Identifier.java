    package compiler.Parser.StatementsAndExpressions;

    import compiler.Semantic.Visitor;

    public class Identifier extends Expression {
        String name;
        public Identifier(String name) {
            this.name = name;
            this.kind = "Identifier";
        }

        public String getName(){
            return this.name;
        }
        public String toString() {
            return "{\n" +
                    "\tkind: " + this.kind + ",\n" +
                    "\tname: " + this.name + "\n" +
                    "\ttype: " + this.getType() + "\n" +
                    "}";
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Identifier other){
                return this.name.equals(other.name) && this.kind.equals(other.kind);
            }
            return false;
        }
    }