package pathfinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.scene.control.Button;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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
import javafx.stage.Stage;

class NodeButton extends Button implements Comparable<NodeButton>{
	int row = 0;
	int col = 0;
	int nodeValue = 0;
	boolean isVisited = false;
	int fcost = 0;
	int hcost = 0;
	int gcost = 0; 
	NodeButton parent = null;
	ImageView nodeCover = new ImageView(new Image("file:res/node.jpg"));
	ImageView wallCover = new ImageView(new Image("file:res/wall.jpg"));
	ImageView start = new ImageView(new Image("file:res/start.png"));
	ImageView goal = new ImageView(new Image("file:res/goal.png"));
	ImageView checked = new ImageView(new Image("file:res/checked.png"));
	ImageView path = new ImageView(new Image("file:res/path.png"));
	NodeButton(int row, int col, int nodeValue){
		this.row = row;
		this.col = col;
		this.nodeValue = nodeValue;
		double size = 25;
		setMinWidth(size); 
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);
		nodeCover.setFitWidth(size-1);
		nodeCover.setFitHeight(size-1);
		wallCover.setFitWidth(size-1);
		wallCover.setFitHeight(size-1);
		start.setFitWidth(size-1);
		start.setFitHeight(size-1);
		goal.setFitWidth(size-1);
		goal.setFitHeight(size-1);
		checked.setFitWidth(size-1);
		checked.setFitHeight(size-1);
		path.setFitWidth(size-1);
		path.setFitHeight(size-1);
		setGraphic(nodeCover);	
	}
	@Override
	public String toString() {
		return "node row: " + this.row + " node col:  " + this.col;
	}
	@Override 
	public int compareTo(NodeButton b) {
		if (this.fcost > b.fcost) {
			return 1;
		}
		if (this.fcost == b.fcost) {
			return 0;
		}
		if(this.fcost < b.fcost) {
			return -1;
		}
		return 0;
	}
}
