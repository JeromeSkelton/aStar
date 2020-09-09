import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 * A Segment is the most interesting class making up our graph, and represents
 * an edge between two Nodes. It knows the Road it belongs to as well as the
 * Nodes it joins, and contains a series of Locations that make up the length of
 * the Segment and can be used to render it.
 * 
 * @author tony
 */
public class Segment {
	public final Map<Integer, Double> speedChart = new HashMap<Integer, Double>() {{
		put(0,5.00);
		put(1,20.00);
		put(2,40.00);
		put(3,60.00);
		put(4,80.00);
		put(5,100.00);
		put(6,110.00);
		put(7,0.00);
	}};

	public final Road road;
	public final Node start, end;
	public final double length;
	public final Location[] points;
	public double timeToTravel;

	public Segment(Graph graph, int roadID, double length, int node1ID,
			int node2ID, double[] coords) {

		this.road = graph.roads.get(roadID);
		this.start = graph.nodes.get(node1ID);
		this.end = graph.nodes.get(node2ID);
		this.length = length;

		points = new Location[coords.length / 2];
		for (int i = 0; i < points.length; i++) {
			points[i] = Location
					.newFromLatLon(coords[2 * i], coords[2 * i + 1]);
		}

		this.road.addSegment(this);
		this.start.addSegment(this);
		this.end.addSegment(this);
		this.timeToTravel = (length / speedChart.get(road.getSpeed())) * 60.00;
	}

	public void draw(Graphics g, Location origin, double scale) {
		for (int i = 1; i < points.length; i++) {
			Point p = points[i - 1].asPoint(origin, scale);
			Point q = points[i].asPoint(origin, scale);
			g.drawLine(p.x, p.y, q.x, q.y);
		}
	}

	//Helper methods
	public double getLength() { return this.length;}

	public Node getEnd() {return this.end;}

	public Node getStart() {return this.start;}

	public Road getRoad() { return this.road;}

	public double travelTime() { return this.timeToTravel;}
}

// code for COMP261 assignments