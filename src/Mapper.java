import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

/**
 * This is the main class for the mapping program. It extends the GUI abstract
 * class and implements all the methods necessary, as well as having a main
 * function.
 * 
 * @author tony
 */
public class Mapper extends GUI {
	public static final Color NODE_COLOUR = new Color(77, 113, 255);
	public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);
	public static final Color HIGHLIGHT_COLOUR = new Color(255, 219, 77);

	// these two constants define the size of the node squares at different zoom
	// levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
	// log(scale)
	public static final int NODE_INTERCEPT = 1;
	public static final double NODE_GRADIENT = 0.8;

	// defines how much you move per button press, and is dependent on scale.
	public static final double MOVE_AMOUNT = 100;
	// defines how much you zoom in/out per button press, and the maximum and
	// minimum zoom levels.
	public static final double ZOOM_FACTOR = 1.3;
	public static final double MIN_ZOOM = 1, MAX_ZOOM = 200;

	// how far away from a node you can click before it isn't counted.
	public static final double MAX_CLICKED_DISTANCE = 0.15;

	// these two define the 'view' of the program, ie. where you're looking and
	// how zoomed in you are.
	private Location origin;
	private double scale;

	// our data structures.
	private Graph graph;

	// the two highlighted nodes used for calculating distance between each other
	Node hNode1 = null;
	Node hNode2 = null;

	// click switch
	int click = 1;

	//Search
	aStarSearch newSearch;


	@Override
	protected void redraw(Graphics g) {
		if (graph != null)
			graph.draw(g, getDrawingAreaDimension(), origin, scale);
	}

	@Override
	protected void onClick(MouseEvent e) {
		Location clicked = Location.newFromPoint(e.getPoint(), origin, scale);
		// find the closest node.
		double bestDist = Double.MAX_VALUE;
		Node closest = null;

		for (Node node : graph.nodes.values()) {
			double distance = clicked.distance(node.location);
			if (distance < bestDist) {
				bestDist = distance;
				closest = node;
			}
		}

		// if it's close enough, highlight it and show some information.
		if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {
			if (hNode1 == null || hNode2 == null) {
				switch (click) {
					case 1:
						hNode1 = closest;
						hNode1.highlight();
						click = 2;
						getTextOutputArea().setText("From - " + hNode1.toString() + "\n\nTo - Select destination node");
						break;

					case 2:
						if(!closest.equals(hNode1)) {
							hNode2 = closest;
							hNode2.highlight();
							click = 1;
							getTextOutputArea().setText("From - " + hNode1.toString() + "\n\nTo - " + hNode2.toString());

							//A star search
							graph.unVisitAll();
							newSearch = new aStarSearch();
							Stack<Segment> segsToHighlight = newSearch.findPath(hNode1,hNode2);
							graph.highlightSegments(segsToHighlight);

							if (segsToHighlight.isEmpty()) {
								getTextOutputArea().append("\n\nNo path between these two points");
							} else {
								getTextOutputArea().append("\n\nRoads Traveled (Shortest Distance)");
								Map<String, Double> roadLengths = newSearch.getRoadLengths();
								double totDistance = 0;
								for (String s : roadLengths.keySet()) {
									getTextOutputArea().append("\n" + s + ": " + Math.round(roadLengths.get(s)*100.0)/100.0 + "km");
									totDistance += roadLengths.get(s);
								}
								getTextOutputArea().append("\n\nTotal Distance: " + Math.round(totDistance*100.0)/100.0 + "km");
							}
						}
						break;

				}
			} else {
				graph.unHighlightAll();
				hNode1 = closest;
				hNode1.highlight();
				click = 2;
				getTextOutputArea().setText("From - " + hNode1.toString() + "\n\nTo - Select destination node");
				hNode2 = null;
			}
		} else {
			getTextOutputArea().setText("Select any two nodes to calculate distance between them");
			graph.unHighlightAll();
			hNode1 = null;
			hNode2 = null;
			click = 1;
		}
	}

	@Override
	protected void onSearch() {
		JTextField userInput = getSearchBox();
		String search = userInput.getText();

		if (hNode1 != null && hNode2 != null && search.equals("speed")) {
			graph.unVisitAll();
			newSearch = new aStarSearch();
			newSearch.bySpeed();
			Stack<Segment> segsToHighlight = newSearch.findPath(hNode1,hNode2);
			graph.highlightSegments(segsToHighlight);

			if (segsToHighlight.isEmpty()) {
				getTextOutputArea().append("\n\nNo path between these two points");
			} else {
				getTextOutputArea().append("\n\nRoads Traveled (Fastest Route)");
				Map<String, Double> roadLengths = newSearch.getRoadLengths();
				double totDistance = 0;
				for (String s : roadLengths.keySet()) {
					getTextOutputArea().append("\n" + s + ": " + Math.round(roadLengths.get(s)*100.0)/100.0 + "km");
					totDistance += roadLengths.get(s);
				}
				getTextOutputArea().append("\n\nTotal Distance: " + Math.round(totDistance*100.0)/100.0 + "km");
			}
		}
	}

	@Override
	protected void onMove(Move m) {
		if (m == GUI.Move.NORTH) {
			origin = origin.moveBy(0, MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.SOUTH) {
			origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.EAST) {
			origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.WEST) {
			origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.ZOOM_IN) {
			if (scale < MAX_ZOOM) {
				// yes, this does allow you to go slightly over/under the
				// max/min scale, but it means that we always zoom exactly to
				// the centre.
				scaleOrigin(true);
				scale *= ZOOM_FACTOR;
			}
		} else if (m == GUI.Move.ZOOM_OUT) {
			if (scale > MIN_ZOOM) {
				scaleOrigin(false);
				scale /= ZOOM_FACTOR;
			}
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		graph = new Graph(nodes, roads, segments, polygons);
		origin = new Location(-250, 250); // close enough
		scale = 1;
	}

	/**
	 * This method does the nasty logic of making sure we always zoom into/out
	 * of the centre of the screen. It assumes that scale has just been updated
	 * to be either scale * ZOOM_FACTOR (zooming in) or scale / ZOOM_FACTOR
	 * (zooming out). The passed boolean should correspond to this, ie. be true
	 * if the scale was just increased.
	 */
	private void scaleOrigin(boolean zoomIn) {
		Dimension area = getDrawingAreaDimension();
		double zoom = zoomIn ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;

		int dx = (int) ((area.width - (area.width * zoom)) / 2);
		int dy = (int) ((area.height - (area.height * zoom)) / 2);

		origin = Location.newFromPoint(new Point(dx, dy), origin, scale);
	}

	public static void main(String[] args) {
		new Mapper();
	}
}

// code for COMP261 assignments