import java.util.*;

public class aStarSearch {
    PriorityQueue<aTuple> fringe = new PriorityQueue<>();
    Map<String, Double> roadLengths = new HashMap<>();
    Stack<Segment> segTraveled = new Stack<>();
    double gCostTotal = 0;
    boolean bySpeed = false;

    public Stack<Segment> findPath(Node start, Node goal) {
        fringe.offer(new aTuple(start, null, 0, findH(start, goal, null),null));

        while(!fringe.isEmpty()) {
            aTuple currTuple = fringe.poll();
            Node curr = currTuple.getCurrNode();
            gCostTotal = currTuple.getGCost();

            if (!curr.isVisited()) {
                curr.visit();

                if (curr == goal) {
                    while (currTuple.getPrevTuple() != null) {
                        if (roadLengths.containsKey(currTuple.getLink().getRoad().getName())) {
                            roadLengths.replace(currTuple.getLink().getRoad().getName(), roadLengths.get(currTuple.getLink().getRoad().getName()) + currTuple.getLink().length);
                        } else {
                            roadLengths.put(currTuple.getLink().getRoad().getName(), currTuple.getLink().getLength());
                        }

                        segTraveled.push(currTuple.getLink());
                        currTuple = currTuple.getPrevTuple();
                    }
                    return segTraveled;
                }

                for (Segment link : curr.getSegments()) {
                    Node neighbour = null;
                    if (curr == link.getStart()) {
                        neighbour = link.getEnd();
                    } else if (!link.getRoad().isOneWay()){
                        neighbour = link.getStart();
                    }

                    if(neighbour != null) {
                        if (!neighbour.isVisited()) {
                                fringe.offer(new aTuple(neighbour, currTuple, findG(link), findG(link) + findH(neighbour, goal, link), link));
                            }
                        }
                    }
                }
            }
        return segTraveled;
    }

    public double findG(Segment link) {
        return link.getLength() + gCostTotal;
    }

    public double findH(Node curr, Node goal, Segment link) {
        double speedCost = 0;
        if (link != null) {
            if (bySpeed && link.getRoad().getSpeed() != 7) {
                speedCost = link.travelTime();
            }
        }
        return curr.getLoc().distance(goal.getLoc()) + speedCost;
    }

    public Map<String, Double> getRoadLengths() {return this.roadLengths;}

    public void bySpeed() {this.bySpeed = true;}
}
