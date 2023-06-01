import java.io.*;
import java.util.*;
import java.awt.*;
import java.util.List;

public class IMECEPathFinder{
    public int[][] grid;
    public int height, width;
    public int maxFlyingHeight;
    public double fuelCostPerUnit, climbingCostPerUnit;

    public IMECEPathFinder(String filename, int rows, int cols, int maxFlyingHeight, double fuelCostPerUnit, double climbingCostPerUnit){

        grid = new int[rows][cols];
        this.height = rows;
        this.width = cols;
        this.maxFlyingHeight = maxFlyingHeight;
        this.fuelCostPerUnit = fuelCostPerUnit;
        this.climbingCostPerUnit = climbingCostPerUnit;

        // TODO: fill the grid variable using data from filename
        // TODO: submitlenmeden önce path düzeltilecek !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        File map_file = new File("sample_IO/sample_1/"+filename);
        Scanner scanner;
        try {
            scanner = new Scanner(map_file);
            int y = 0;
            int x = 0;
            while (scanner.hasNextInt()) {
                grid[y][x++] = scanner.nextInt();
                if (x == cols) {
                    x = 0;
                    y++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public double CalculateAdjCost(Point start, Point end){ // TODO hatalı olabilir kontrol edersin
        try {
            double h_diff = grid[end.getY()][end.getX()] - grid[start.getY()][start.getX()];
            double dist = Math.sqrt(Math.pow(start.getX() - end.getX(),2.0) + Math.pow(start.getY() - end.getY(),2.0));
            return (dist*fuelCostPerUnit) + ((Math.max(h_diff, 0.0))*climbingCostPerUnit);
        }
        catch(ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException(""+start+end);
        }

    }

    public double CalculatePathCost(List<Point> pathPointsList, int mission_num){
        double total = 0.0;
        for(int i = 1; i < pathPointsList.size(); i++) {
            Point start = pathPointsList.get(i-1);
            Point end = pathPointsList.get(i);
            double h_diff = grid[end.getY()][end.getX()] - grid[start.getY()][start.getX()];
            h_diff = mission_num == 1?Math.abs(h_diff):h_diff;
            double dist = 0.0;
            if (mission_num == 0)
                dist = Math.sqrt(Math.pow(start.getX() - end.getX(),2.0) + Math.pow(start.getY() - end.getY(),2.0));
            total = total + (dist*fuelCostPerUnit) + ((Math.max(h_diff, 0.0))*(mission_num == 1?1:climbingCostPerUnit));
        }
        return total;
    }

    /**
     * Draws the grid using the given Graphics object.
     * Colors should be grayscale values 0-255, scaled based on min/max elevation values in the grid
     */
    public void drawGrayscaleMap(Graphics g){

        // TODO: draw the grid, delete the sample drawing with random color values given below
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int[] ints : grid)
            for (int x = 0; x < ints.length; x++) {
                min = Math.min(ints[x], min);
                max = Math.max(ints[x], max);
            }
        double s_const = 255.0/(max-min);
        for (int i = 0; i < grid.length; i++)
        {
            for (int j = 0; j < grid[i].length; j++) {
                int value = (int)((grid[i][j]-min)*s_const);
                g.setColor(new Color(value, value, value));
                g.fillRect(j, i, 1, 1);
            }
        }
    }

    /**
     * Get the most cost-efficient path from the source Point start to the destination Point end
     * using Dijkstra's algorithm on pixels.
     * @return the List of Points on the most cost-efficient path from start to end
     */
    public List<Point> getMostEfficientPath(Point start, Point end) {

        List<Point> path = new ArrayList<>();
        int total_vertex = grid.length*grid[0].length;
        double[] _1d_grid = new double[total_vertex];
        Edge[] _1d_edge = new Edge[total_vertex];
        Arrays.fill(_1d_grid,Double.POSITIVE_INFINITY);
        PriorityQueue<Integer> PQ = new PriorityQueue<Integer>(total_vertex,Comparator.comparingDouble(o -> _1d_grid[o])); // TODO checklersin doğru mu diye

        _1d_grid[GetOneDimensionEqual(start)] = 0.0;

        PQ.add(GetOneDimensionEqual(start));

        while (!PQ.isEmpty())
        {
            int v = PQ.poll();
            for (Edge e : get_adj_list(ConvertOneToTwo(v)))
                relax(e,_1d_grid,_1d_edge,PQ);
        }
        Point searching = end;
        path.add(0,searching);
        try {
            while ((searching = _1d_edge[GetOneDimensionEqual(searching)].source) != start)
                path.add(0,searching);
        }
        catch(NullPointerException e){
//            System.out.println("hmm");
        }
//        System.out.println("bulduu");
//        System.out.println(path);
        // TODO: Your code goes here
        // TODO: Implement the Mission 0 algorithm here

        return path;
    }

    public void relax(Edge e, double[] _1d_grid, Edge[] _1d_edge, PriorityQueue<Integer> PQ) {
        int v = GetOneDimensionEqual(e.getSource());
        int w = GetOneDimensionEqual(e.getDest());
        if (_1d_grid[w] > _1d_grid[v] + e.getCost())
        {
            _1d_grid[w] = _1d_grid[v] + e.getCost();
            _1d_edge[w] = e;
            if (PQ.contains(w)){
                PQ.remove(w);
                PQ.add(w);
            }
            else
                PQ.add(w);
        }
    }
    public int GetOneDimensionEqual(Point point){
        return point.getY()*grid.length+point.getX();
    }

    public Point ConvertOneToTwo(int one_dim_equal){
        int y = Math.floorDiv(one_dim_equal, grid.length);
        int x = one_dim_equal % grid[0].length;
        return new Point(x,y);
    }

    /**
     * Calculate the most cost-efficient path from source to destination.
     * @return the total cost of this most cost-efficient path when traveling from source to destination
     */
    public double getMostEfficientPathCost(List<Point> path){
        double totalCost = CalculatePathCost(path,0);;

        // TODO: Your code goes here, use the output from the getMostEfficientPath() method

        return totalCost;
    }


    /**
     * Draw the most cost-efficient path on top of the grayscale map from source to destination.
     */
    public void drawMostEfficientPath(Graphics g, List<Point> path){
        // TODO: Your code goes here, use the output from the getMostEfficientPath() method
        g.setColor(new Color(0, 255, 0));
        for(Point p : path)
            g.fillRect(p.getX(), p.getY(), 1, 1);
    }

    // TODO mission0 ve mission1 icin farkli method yazabilirsin maxFlyingHeight icin belkide gerek yoktur
    public Point get_adj_coord(Point cur_pos, int yplus, int xplus){
        int y = cur_pos.getY() + yplus;
        int x = cur_pos.getX() + xplus;
        if ((((y > -1)&&(y < grid.length)) && ((x>-1)&&(x < grid[y].length))) && (grid[y][x] <= maxFlyingHeight))
            return new Point(x,y);
        return null;
    }


    public ArrayList<Edge> get_adj_list(Point point){
        ArrayList<Edge> adj_list = new ArrayList<>();
        ArrayList<Point> adj_coords = new ArrayList<>();

        // Have to be that order
        adj_coords.add(get_adj_coord(point, 0,-1));
        adj_coords.add(get_adj_coord(point, 0,1));
        adj_coords.add(get_adj_coord(point, -1,0));
        adj_coords.add(get_adj_coord(point, +1,0));
        adj_coords.add(get_adj_coord(point, 1,-1));
        adj_coords.add(get_adj_coord(point, -1,-1));
        adj_coords.add(get_adj_coord(point, 1,1));
        adj_coords.add(get_adj_coord(point, -1,1));
        for(Point i : adj_coords)
            if ((i != null) && (i.getX() < grid[0].length) && (i.getY() < grid.length))
                adj_list.add(new Edge(point, i, this));

        return adj_list;


//        West, (x-1)
//        East, (x+1)
//        North, (y-1)
//        South, (y+1)
//        South West, (x-1, y+1)
//        North West, (x-1, y-1)
//        South East, (x+1, y+1)
//        North East. (x+1, y-1)
    }

    /**
     * Find an escape path from source towards East such that it has the lowest elevation change.
     * Choose a forward step out of 3 possible forward locations, using greedy method described in the assignment instructions.
     * @return the list of Points on the path
     */
    public List<Point> getLowestElevationEscapePath(Point start){
        List<Point> pathPointsList = new ArrayList<>();
        pathPointsList.add(start);
        Point cur_pos = new Point(start.getX(),start.getY());
        Point E, SE, NE;
        while((E = get_adj_coord(cur_pos, 0, 1)) != null) {
            NE = get_adj_coord(cur_pos, -1, 1);
            SE = get_adj_coord(cur_pos, 1, 1);
            Point choosen_dest = E;
            if ((NE != null) && (Math.abs(grid[cur_pos.getY()][cur_pos.getX()] - grid[NE.getY()][NE.getX()]) < Math.abs(grid[cur_pos.getY()][cur_pos.getX()] - grid[choosen_dest.getY()][choosen_dest.getX()])))
                choosen_dest = NE;
            if ((SE != null) && (Math.abs(grid[cur_pos.getY()][cur_pos.getX()] - grid[SE.getY()][SE.getX()]) < Math.abs(grid[cur_pos.getY()][cur_pos.getX()] - grid[choosen_dest.getY()][choosen_dest.getX()])))
                choosen_dest = SE;
            pathPointsList.add(choosen_dest);
            cur_pos = choosen_dest;
        }
        // TODO: Your code goes here
        // TODO: Implement the Mission 1 greedy approach here

        return pathPointsList;
    }


    /**
     * Calculate the escape path from source towards East such that it has the lowest elevation change.
     * @return the total change in elevation for the entire path
     */
    public int getLowestElevationEscapePathCost(List<Point> pathPointsList){
        double totalChange = 0;
        return (int) CalculatePathCost(pathPointsList,1);
    }


    /**
     * Draw the escape path from source towards East on top of the grayscale map such that it has the lowest elevation change.
     */
    public void drawLowestElevationEscapePath(Graphics g, List<Point> pathPointsList){
        g.setColor(new Color(255, 255, 0));
        for(Point p : pathPointsList)
            g.fillRect(p.getX(), p.getY(), 1, 1);
        // TODO: Your code goes here, use the output from the getLowestElevationEscapePath() method
    }


}
