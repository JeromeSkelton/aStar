import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.*;

/**
 * This represents the data structure storing all the roads, nodes, and
 * segments, as well as some information on which nodes and segments should be
 * highlighted.
 * 
 * @author tony
 */
public class Graph {
	// map node IDs to Nodes.
	Map<Integer, Node> nodes = new HashMap<>();
	// map road IDs to Roads.
	Map<Integer, Road> roads;
	// just some collection of Segments.
	Collection<Segment> segments;

	Node selectedNode;
	Collection<Road> highlightedRoads = new HashSet<>();
	Stack<Segment> highlightedSegments = new Stack<>();

	public Graph(File nodes, File roads, File segments, File polygons) {
		this.nodes = Parser.parseNodes(nodes, this);
		this.roads = Parser.parseRoads(roads, this);
		this.segments = Parser.parseSegments(segments, this);
	}

	public void draw(Graphics g, Dimension screen, Location origin, double scale) {
		// a compatibility wart on swing is that it has to give out Graphics
		// objects, but Graphics2D objects are nicer to work with. Luckily
		// they're a subclass, and swing always gives them out anyway, so we can
		// just do this.
		Graphics2D g2 = (Graphics2D) g;

		// draw all the segments.
		g2.setColor(Mapper.SEGMENT_COLOUR);
		for (Segment s : segments)
			s.draw(g2, origin, scale);

		// draw the segments of all highlighted roads.
		g2.setColor(Mapper.HIGHLIGHT_COLOUR);
		g2.setStroke(new BasicStroke(3));
		for (Road road : highlightedRoads) {
			for (Segment seg : road.components) {
				seg.draw(g2, origin, scale);
			}
		}

		// Segment highlight
		g2.setColor(Mapper.HIGHLIGHT_COLOUR);
		g2.setStroke(new BasicStroke(3));
		for (Segment seg : highlightedSegments) {
			seg.draw(g2, origin, scale);
		}

		// draw all the nodes.
		for (Node n : nodes.values()) {
			if (n.isHighlighted()) {
				g2.setColor(Mapper.HIGHLIGHT_COLOUR);
			} else {
				g2.setColor(Mapper.NODE_COLOUR);
			}
			n.draw(g2, screen, origin, scale);
		}
	}

	// Set highlight for roads
	public void setHighlight(Collection<Road> roads) { this.highlightedRoads = roads; }

	public void unVisitAll() {
		for (Node n : nodes.values())
			if(n.isVisited()) {
				n.unvisit();
			}
	}

	public void unHighlightAll() {
		this.selectedNode = null;
		this.highlightedSegments.clear();
		for (Node n : nodes.values()) {
			if (n.isHighlighted()) {
				n.highlight();
			}
		}
	}

	// Set highlight for segments
	public void highlightSegments(Stack<Segment> s) { this.highlightedSegments = s;}
}

// code for COMP261 assignments