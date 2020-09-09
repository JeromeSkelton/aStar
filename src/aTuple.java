public class aTuple implements Comparable<aTuple>{
    Node currNode;
    aTuple prevTuple;
    double gCost;
    double fCost;
    Segment link;

    aTuple(Node c, aTuple p, double g, double f, Segment s) {
        this.currNode = c;
        this.prevTuple = p;
        this.gCost = g;
        this.fCost = f;
        this.link = s;
    }

    @Override
    public int compareTo(aTuple o) {
        if (this.fCost < o.fCost) return -1;
        if (this.fCost > o.fCost) return 1;
        return 0;
    }

    //helper methods
    public Node getCurrNode() {return this.currNode;}

    public aTuple getPrevTuple() {return this.prevTuple;}

    public double getGCost() {return this.gCost;}

    public Segment getLink() {return this.link;}
}
