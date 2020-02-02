package pathfinding;
import java.awt.List;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import com.sun.glass.events.KeyEvent;

import javafx.scene.shape.Rectangle;

import javafx.scene.control.Button;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Pathfindingvisualizer extends Application{
	static int gridHeight = 25;
	static int gridWidth = 25;
	int startRow = 10;
	int startCol = 5;
	int endRow = 10;
	int endCol = 15;
	NodeButton[][] nodeGrid = new NodeButton[gridWidth][gridHeight];
	int[][] board = new int[gridWidth][gridHeight];
	boolean sHeld;
	boolean eHeld;
	int iter = 0;
	boolean endset = true;
	boolean startset = true;
	boolean hasRun = false;
	boolean noPath = false;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	//setup for stage 
	public void start(Stage theStage) throws Exception {
		theStage.setTitle("pathfinding");
		GridPane grid = new GridPane();
		BorderPane border = new BorderPane();
		HBox top = new HBox(10);
		Button visualize = new Button("start visulization");
		Button reset = new Button("reset board");
		Button clearPaths = new Button("clear paths (walls are left)");
		Button visualizesteps = new Button("visualize steps");
		Label nopath = new Label("**All available nodes have been checked, no path was found**"); 
		Popup popup1 = new Popup(); 
		popup1.getContent().add(nopath);
		popup1.setAutoHide(true); 
		top.setAlignment(Pos.CENTER);
		border.setTop(top);
		border.setCenter(grid);
		grid.setAlignment(Pos.CENTER);
		top.getChildren().add(visualize);
		top.getChildren().add(visualizesteps);
		top.getChildren().add(clearPaths);
		top.getChildren().add(reset);
		for(int row = 0; row < gridHeight; row++) {
			for(int col = 0; col < gridWidth; col++) {
				nodeGrid[row][col] = new NodeButton(row,col,0);
				NodeButton n = nodeGrid[row][col];
				if(row == startRow && col == startCol) {
					n.setGraphic(n.start);
					n.nodeValue = 2;
				}
				if(row == endRow && col == endCol) {
					n.setGraphic(n.goal);
					n.nodeValue = 3;
				}
				visualize.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						clearBoard(nodeGrid);
						Astar(nodeGrid, nodeGrid[startRow][startCol]);
						if(noPath == true) {
							popup1.show(theStage);
						}
					}

				});
				visualizesteps.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						iter++;
						Astarsteps(nodeGrid, nodeGrid[startRow][startCol], iter);
					}
				});
				reset.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						noPath = false;
						hasRun = false;
						iter = 0;
						for(int row = 0; row < gridHeight; row++) {
							for(int col = 0; col < gridWidth; col++) {
								if(nodeGrid[row][col].nodeValue == 1 || nodeGrid[row][col].nodeValue == 4) {
									nodeGrid[row][col].nodeValue = 0;
									nodeGrid[row][col].setGraphic(nodeGrid[row][col].nodeCover);
								}

							}
						}
					}

				});
				clearPaths.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						noPath = false;
						hasRun = false;
						iter = 0;
						clearBoard(nodeGrid);
					}
				});
				//from https://stackoverflow.com/questions/30117351/how-to-detect-mouse-movement-over-node-while-button-is-pressed
				grid.addEventFilter(MouseEvent.DRAG_DETECTED , new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent mouseEvent) {
						grid.startFullDrag();
					}
				});
				//changes the empty nodes to a wall when the mouse is dragged into the node and vice versa
				n.setOnMouseDragEntered(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent t) {
						switch(n.nodeValue) {
						case(0):
							n.setGraphic(n.wallCover);
						n.nodeValue = 1;
						break;
						case(1):
							n.setGraphic(n.nodeCover);
						n.nodeValue = 0;
						break;
						}

					}
				});
				//handle a single click on a node depending on the state of the node
				n.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent t) {
						if(t.getButton()==MouseButton.PRIMARY) {
							switch(n.nodeValue) {
							case(0):
								if(startset == false) {
									startRow = n.row;
									startCol = n.col;
									n.nodeValue = 2;
									n.setGraphic(n.start);
									startset = true;
								}
								else if(endset == false) {
									endRow = n.row;
									endCol = n.col;
									n.nodeValue = 3;
									n.setGraphic(n.goal);
									endset = true;
									clearBoard(nodeGrid);
									if(hasRun == true)
										Astar(nodeGrid,nodeGrid[startRow][startCol]);
								}
								else if(endset == true && startset == true){
									n.setGraphic(n.wallCover);
									n.nodeValue = 1;
								}
							break;
							case(1):
								n.setGraphic(n.nodeCover);
							n.nodeValue = 0;
							break;
							case(2):
								if(endset == true && startset == true) {
									n.setGraphic(n.nodeCover);
									n.nodeValue = 0;
									startset = false;
								}
								else if(endset == false) {
									break;
								}
							break;
							case(3):
								if(endset == true && startset == true) {
									n.setGraphic(n.nodeCover);
									n.nodeValue = 0;
									endset = false;
								}
								else if(startset == false) {
									break;
								}
							break;
							case(4):
								System.out.println("Node G cost: " + n.gcost + " Node H cost: " + n.hcost + " Node F cost: " + n.fcost);
							System.out.println(n.row + " " + n.col);
							break;
							}
						}
					}
				});
				//add nodes to the grid pane
				grid.add(nodeGrid[row][col], col, row);
			}
		}
		theStage.setScene(new Scene(border));
		theStage.show();
	}
	//run the A* algorithm up to the number of times that the step visualize button has been pressed
	public void Astarsteps(NodeButton[][] n, NodeButton currNode, int num) {
		hasRun = true;
		ArrayList<NodeButton> open = new ArrayList<NodeButton>(); //list of "open" nodes that have been found
		ArrayList<NodeButton> closed = new ArrayList<NodeButton>(); //list of all nodes that have been "closed"
		for(int i = 0; i < iter; i++) {
			if(open.contains(n[endRow][endCol])) {
				break;
			}
			open.add(currNode);
			addOpenNodes(n, currNode, open, closed);
			closed.add(currNode);
			open = removeNodes(open, currNode);
			assignCosts(n, currNode, open);
			Collections.sort(open);
			/*
			if(open.size() == 1) {
				System.out.println("open empty");
				noPath = true;
				break;
			}
			 */
			currNode = open.get(0);
		}
		animateAstarSteps(open, closed, n, currNode);
		n[endRow][endCol].parent = null;
	}
	public void Astar(NodeButton[][] n, NodeButton currNode) {
		hasRun = true;
		int count = 0;
		int oneOpenCount = 0;
		long startTime = System.currentTimeMillis();
		ArrayList<NodeButton> open = new ArrayList<NodeButton>();
		ArrayList<NodeButton> closed = new ArrayList<NodeButton>();
		while(!open.contains(n[endRow][endCol])){
			open.add(currNode);
			addOpenNodes(n, currNode, open, closed);
			closed.add(currNode);
			open = removeNodes(open, currNode);
			assignCosts(n, currNode, open);
			Collections.sort(open);
			if(count > 0 && open.size() <= 1) {
				oneOpenCount++;
				System.out.println(oneOpenCount);
				if(oneOpenCount > 0) {				
					System.out.println("open empty");
					noPath = true;
					break;
				}
			}
			count++;
			currNode = open.get(0);
		}
		animateAstar(open, closed, n);
		if(open.contains(n[endRow][endCol])) {
			System.out.println("found end node");
		}
		long endTime = System.currentTimeMillis();
		System.out.println("completed in " + (endTime - startTime) + " miliseconds");
		n[endRow][endCol].parent = null;
	}
	//examine nodes around the current node and add them to an open list, accounting for any edge cases, and disallowing the passage of the algorithm through two blocks placed diagonally adjacent to each other
	public void addOpenNodes(NodeButton[][] nodeGrid, NodeButton currNode, ArrayList<NodeButton> open, ArrayList<NodeButton> closed) {
		ArrayList<NodeButton> unWalkable = new ArrayList<NodeButton>();
		if(currNode.row - 1 >= 0 && currNode.col - 1 >= 0 && currNode.row + 1 <= 24 && currNode.col + 1 <= 24) {
			if(nodeGrid[currNode.row + 1][currNode.col].nodeValue == 1 && nodeGrid[currNode.row][currNode.col + 1].nodeValue == 1) {
				unWalkable.add(nodeGrid[currNode.row + 1][currNode.col + 1]);
			}
			if(nodeGrid[currNode.row + 1][currNode.col].nodeValue == 1 && nodeGrid[currNode.row][currNode.col - 1].nodeValue == 1) {
				unWalkable.add(nodeGrid[currNode.row + 1][currNode.col - 1]);
			}
			if(nodeGrid[currNode.row - 1][currNode.col].nodeValue == 1 && nodeGrid[currNode.row][currNode.col + 1].nodeValue == 1) {
				unWalkable.add(nodeGrid[currNode.row - 1][currNode.col + 1]);
			}
			if(nodeGrid[currNode.row - 1][currNode.col].nodeValue == 1 && nodeGrid[currNode.row][currNode.col - 1].nodeValue == 1) {
				unWalkable.add(nodeGrid[currNode.row - 1][currNode.col - 1]);
			}
			for(int i = currNode.row - 1; i <= currNode.row + 1; i++) {
				for(int j = currNode.col - 1; j <= currNode.col + 1; j++) {
					//if node is an un-walkable node, continue looking for nodes
					if(nodeGrid[i][j].nodeValue == 1 || nodeGrid[i][j] == currNode || closed.contains(nodeGrid[i][j])) {
						continue;
					}
					//if the node being examined is already in the open list recalculate costs if necessary
					if(open.contains(nodeGrid[i][j])) {
						recalculateGCosts(nodeGrid[i][j], currNode);
						continue;
					}
					if(unWalkable.contains(nodeGrid[i][j])) {
						continue;
					}
					nodeGrid[i][j].parent = currNode;
					nodeGrid[i][j].isVisited = true;
					open.add(nodeGrid[i][j]);
					System.out.println("added open node " + nodeGrid[i][j]);
				}
			}
		}
		else if(currNode.row - 1 < 0 && currNode.col - 1 < 0) {
			for(int i = currNode.row; i <= currNode.row + 1; i++) {
				for(int j = currNode.col; j <= currNode.col + 1; j++) {
					if(nodeGrid[i][j].nodeValue == 1 || nodeGrid[i][j] == currNode || closed.contains(nodeGrid[i][j])) {
						continue;
					}
					if(open.contains(nodeGrid[i][j])) {
						recalculateGCosts(nodeGrid[i][j], currNode);
						continue;
					}
					if(unWalkable.contains(nodeGrid[i][j])) {
						continue;
					}
					nodeGrid[i][j].parent = currNode;
					nodeGrid[i][j].isVisited = true;
					open.add(nodeGrid[i][j]);
				}
			}
		}
		else if(currNode.row + 1 > 24 && currNode.col +1 > 24) {
			for(int i = currNode.row-1; i <= currNode.row; i++) {
				for(int j = currNode.col - 1; j <= currNode.col; j++) {
					if(nodeGrid[i][j].nodeValue == 1 || nodeGrid[i][j] == currNode || closed.contains(nodeGrid[i][j])) {
						continue;
					}
					if(open.contains(nodeGrid[i][j])) {
						recalculateGCosts(nodeGrid[i][j], currNode);
						continue;
					}
					if(unWalkable.contains(nodeGrid[i][j])) {
						continue;
					}
					nodeGrid[i][j].parent = currNode;
					nodeGrid[i][j].isVisited = true;
					open.add(nodeGrid[i][j]);
				}
			}
		}
		else if(currNode.row + 1 > 24 && currNode.col - 1 < 0) {
			for(int i = currNode.row - 1; i <= currNode.row; i++) {
				for(int j = currNode.col; j <= currNode.col + 1; j++) {
					if(nodeGrid[i][j].nodeValue == 1 || nodeGrid[i][j] == currNode || closed.contains(nodeGrid[i][j])) {
						continue;
					}
					if(open.contains(nodeGrid[i][j])) {
						recalculateGCosts(nodeGrid[i][j], currNode);
						continue;
					}
					if(unWalkable.contains(nodeGrid[i][j])) {
						continue;
					}
					nodeGrid[i][j].parent = currNode;
					nodeGrid[i][j].isVisited = true;
					open.add(nodeGrid[i][j]);
				}
			}
		}
		else if(currNode.row - 1 < 0 && currNode.col + 1 > 24) {
			for(int i = currNode.row; i <= currNode.row + 1; i++) {
				for(int j = currNode.col - 1; j <= currNode.col; j++) {
					if(nodeGrid[i][j].nodeValue == 1 || nodeGrid[i][j] == currNode || closed.contains(nodeGrid[i][j])) {
						continue;
					}
					if(open.contains(nodeGrid[i][j])) {
						recalculateGCosts(nodeGrid[i][j], currNode);
						continue;
					}
					if(unWalkable.contains(nodeGrid[i][j])) {
						continue;
					}
					nodeGrid[i][j].parent = currNode;
					nodeGrid[i][j].isVisited = true;
					open.add(nodeGrid[i][j]);
				}
			}
		}
		else if(currNode.row + 1 > 24) {
			for(int i = currNode.row - 1; i <= currNode.row; i++) {
				for(int j = currNode.col - 1; j <= currNode.col + 1; j++) {
					if(nodeGrid[i][j].nodeValue == 1 || nodeGrid[i][j] == currNode || closed.contains(nodeGrid[i][j])) {
						continue;
					}
					if(open.contains(nodeGrid[i][j])) {
						recalculateGCosts(nodeGrid[i][j], currNode);
						continue;
					}
					if(unWalkable.contains(nodeGrid[i][j])) {
						continue;
					}
					nodeGrid[i][j].parent = currNode;
					nodeGrid[i][j].isVisited = true;
					open.add(nodeGrid[i][j]);
				}
			}
		}
		else if(currNode.col + 1 > 24) {
			for(int i = currNode.row - 1; i <= currNode.row + 1; i++) {
				for(int j = currNode.col - 1; j <= currNode.col; j++) {
					if(nodeGrid[i][j].nodeValue == 1 || nodeGrid[i][j] == currNode || closed.contains(nodeGrid[i][j])) {
						continue;
					}
					if(open.contains(nodeGrid[i][j])) {
						recalculateGCosts(nodeGrid[i][j], currNode);
						continue;
					}
					if(unWalkable.contains(nodeGrid[i][j])) {
						continue;
					}
					nodeGrid[i][j].parent = currNode;
					nodeGrid[i][j].isVisited = true;
					open.add(nodeGrid[i][j]);
				}
			}
		}
		else if(currNode.row - 1 < 0) {
			for(int i = currNode.row; i <= currNode.row + 1; i++) {
				for(int j = currNode.col - 1; j <= currNode.col + 1; j++) {
					if(nodeGrid[i][j].nodeValue == 1 || nodeGrid[i][j] == currNode || closed.contains(nodeGrid[i][j])) {
						continue;
					}
					if(open.contains(nodeGrid[i][j])) {
						recalculateGCosts(nodeGrid[i][j], currNode);
						continue;
					}
					if(unWalkable.contains(nodeGrid[i][j])) {
						continue;
					}
					nodeGrid[i][j].parent = currNode;
					nodeGrid[i][j].isVisited = true;
					open.add(nodeGrid[i][j]);
				}
			}
		}
		else if(currNode.col - 1 < 0) {
			for(int i = currNode.row - 1; i <= currNode.row + 1; i++) {
				for(int j = currNode.col; j <= currNode.col + 1; j++) {
					if(nodeGrid[i][j].nodeValue == 1 || nodeGrid[i][j] == currNode || closed.contains(nodeGrid[i][j])) {
						continue;
					}
					if(open.contains(nodeGrid[i][j])) {
						recalculateGCosts(nodeGrid[i][j], currNode);
						continue;
					}
					if(unWalkable.contains(nodeGrid[i][j])) {
						continue;
					}
					nodeGrid[i][j].parent = currNode;
					nodeGrid[i][j].isVisited = true;
					open.add(nodeGrid[i][j]);
				}
			}
		}
		unWalkable.clear();
	}
	//Assign an F cost to each open node F cost is the sum of the G cost (distance from start) and the H cost (distance to end) 
	public ArrayList<NodeButton> assignCosts(NodeButton[][] n, NodeButton currNode, ArrayList<NodeButton> open) {
		for(NodeButton i:open) {
			if(i.parent == null) {
				continue;
			}
			if(i.row == i.parent.row || i.col == i.parent.col) {
				i.gcost = i.parent.gcost + 10; 
			}
			else {
				i.gcost = i.parent.gcost + 14;
			}
			i.hcost = 10*(Math.abs((endRow - i.row)) + Math.abs((endCol - i.col)));
			i.fcost = i.gcost + i.hcost;
		}
		return open;
	}
	//recalculate g costs if they are smaller than the existing g costs for each open node to ensure the shortest path 
	public void recalculateGCosts(NodeButton checkNode, NodeButton currNode) {
		if(checkNode.col == currNode.col || checkNode.row == currNode.row) {
			if(currNode.gcost + 10 < checkNode.gcost) {
				checkNode.gcost = currNode.gcost + 10;
			}
		}
		else {
			if(currNode.gcost + 14 < checkNode.gcost) {
				checkNode.gcost = currNode.gcost + 14;
			}
		}
	}
	//remove all duplicate nodes from open list before adding the node that is removed to the closed list of nodes
	public ArrayList<NodeButton> removeNodes(ArrayList<NodeButton> open, NodeButton currNode) {
		for(int i = 0; i < open.size(); i++) {
			if(open.get(i) == currNode) {
				open.remove(open.get(i));
			}
		}
		System.out.println("Removed node");
		return open;
	}

	public void animateAstar(ArrayList<NodeButton> open, ArrayList<NodeButton> closed, NodeButton[][] n) {
		for(NodeButton i:open) {
			if(i.nodeValue != 2 && i.nodeValue!= 3) {
				i.setGraphic(i.checked);
				i.nodeValue = 4;
			}
		}
		for (NodeButton j:closed) {
			if(j.nodeValue != 2 && j.nodeValue!= 3) {
				j.setGraphic(j.checked);
				j.nodeValue = 4;
			}
		}
		NodeButton endNode = n[endRow][endCol];
		NodeButton pathStart = endNode.parent;
		if(endNode.parent!= null) {
			pathStart.setGraphic(pathStart.path);
		}
		for(int i = closed.size() - 1; i > 0; i++) {
			NodeButton pathNode = closed.get(i);
			NodeButton pathParent = pathNode.parent;
			i = closed.indexOf(pathParent) - 1;
			if(pathParent.nodeValue != 2 && pathParent.nodeValue != 3)
				pathParent.setGraphic(pathParent.path);
		}
	}
	//takes in the current node so that it can be highlighted for the iteration.
	public void animateAstarSteps(ArrayList<NodeButton> open, ArrayList<NodeButton> closed, NodeButton[][] n, NodeButton currNode) {
		for(NodeButton i:open) {
			if(i.nodeValue != 2 && i.nodeValue!= 3) {
				i.setGraphic(i.checked);
				i.nodeValue = 4;
			}
		}
		for (NodeButton j:closed) {
			if(j.nodeValue != 2 && j.nodeValue!= 3) {
				j.setGraphic(j.checked);
				j.nodeValue = 4;
			}
		}
		NodeButton endNode = n[endRow][endCol];
		NodeButton pathStart = endNode.parent;
		if(endNode.parent!= null) {
			pathStart.setGraphic(pathStart.path);
		}
		for(int i = closed.size() - 1; i > 0; i++) {
			NodeButton pathNode = closed.get(i);
			NodeButton pathParent = pathNode.parent;
			i = closed.indexOf(pathParent) - 1;
			if(pathParent.nodeValue != 2 && pathParent.nodeValue != 3)
				pathParent.setGraphic(pathParent.path);
		}
		if(currNode.nodeValue != 3 && currNode.nodeValue !=2)
			currNode.setGraphic(currNode.path);

	}

	public void clearBoard(NodeButton[][] nodeGrid) {
		for(int row = 0; row < gridHeight; row++) {
			for(int col = 0; col < gridWidth; col++) {
				if(nodeGrid[row][col].nodeValue == 4) {
					nodeGrid[row][col].nodeValue = 0;
					nodeGrid[row][col].setGraphic(nodeGrid[row][col].nodeCover);
				}

			}
		}
	}

}
