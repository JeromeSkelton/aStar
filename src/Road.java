import java.util.Collection;
import java.util.HashSet;

/**
 * Road represents ... a road ... in our graph, which is some metadata and a
 * collection of Segments. We have lots of information about Roads, but don't
 * use much of it.
 * 
 * @author tony
 */
public class Road {
	public final int roadID;
	public final String name, city;
	public final Collection<Segment> components;
	public int oneWay;
	public int speed;

	public Road(int roadID, int type, String label, String city, int oneway,
			int s, int roadclass, int notforcar, int notforpede,
			int notforbicy) {
		this.roadID = roadID;
		this.city = city;
		this.name = label;
		this.components = new HashSet<Segment>();
		this.oneWay = oneway;
		this.speed = s;
	}

	public void addSegment(Segment seg) {
		components.add(seg);
	}

	public String getName() {
		return this.name;
	}

	public int getSpeed() { return this.speed;}

	public boolean isOneWay() {
		if (this.oneWay == 0) {
			return false;
		}
		return true;
	}
}

// code for COMP261 assignments