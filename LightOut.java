//Chattarin Muksakul 6213124
//Wachirawit peerapisarnpon 6213145
//Winn Ladawuthiphan 6213146
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.shortestpath.*;

class State
{
    private boolean [][]light;
    private int lastPress;
    
    public State(boolean [][] s, int l)
    {
        light = s;
        lastPress = l;
    }
    
    public boolean[][] getState()
    {
        return light;
    }
    
    public int getLastPress()
    {
        return lastPress;
    }
    
    public boolean[][] toggle(int n)
    {                
        int n_size = light.length;
        
        boolean[][] intTemp=new boolean[light.length][light.length];
        
        for(int i=0;i<n_size;i++)
        {
            for(int j=0;j<n_size;j++)
            {
                intTemp[i][j] = light[i][j];
            }
        }
        
        int row = (n-1)/n_size, column = (n-1)%n_size;
        
        intTemp[row][column] = intTemp[row][column]==true?false:true;
        if(row-1>-1)//up        
            intTemp[row-1][column] = intTemp[row-1][column]==true?false:true; 
                        
        if(row+1<n_size)//down
            intTemp[row+1][column] = intTemp[row+1][column]==true?false:true;
            
        if(column-1>-1)//left
            intTemp[row][column-1] = intTemp[row][column-1]==true?false:true;
                  
        if(column+1<n_size)//right
            intTemp[row][column+1] = intTemp[row][column+1]==true?false:true;                      
                           
        return intTemp;
    }    
    
    @Override
    public String toString()
    {
        String re="";
        
        for(int i=0;i<light.length;i++)
        {
            for(int j=0;j<light.length;j++)
            {
                re = re + (light[i][j]==true?'1':'0');
            }
        }        
        return re;
    }
}

public class LightOut 
{    
    private int n_size=0;
    private String str_start, ans = ""; 
    private boolean [][]light;
    
    public LightOut()
    {   
        while(n_size<3||n_size>4)
        {
            try
            {
                System.out.println("Number of rows for sqaure grid(3-4) = ");
                Scanner input = new Scanner(System.in);
                n_size = input.nextInt();
                if(n_size<3||n_size>4)
                    System.out.println("Grid size must be 3 or 4");
            }
            catch(Exception ex)
            {
                System.err.println(ex);
            }            
        }
        
        while(true)
        {
            Scanner input2 = new Scanner(System.in);
            try
            {
                System.out.printf("Initial states (%d bits , left to right ,line by line ) = \n", n_size*n_size);
                str_start  = input2.nextLine();                
                int digit = Integer.parseInt(str_start, 2);
                if(str_start.length()==n_size*n_size&&digit>0)
                {
                    for(int i=0;i<n_size*n_size;i++)
                    {
                        ans = ans+"0";
                    }                    
                    break;
                }                    
                           
                if(str_start.length()!=n_size*n_size)
                    System.out.println("current length = " + str_start.length());
                else
                    System.out.println("Initial states must be more than 0");
            } 
            catch(NumberFormatException e) 
            {
                System.err.println("Initial states have only 0 or 1");                
            }         
            catch(Exception ex)
            {
                System.out.println(ex);
            }
        }   
        
        light = new boolean[n_size][n_size];
        
        int temp=0;
        for(int i=0;i<n_size;i++)
        {   
            for(int j=0;j<n_size;j++, temp++)
            {
                light[i][j] = str_start.charAt(temp)=='1'?true:false;                
            }            
        } 
        
        print(str_start);
        solve();
    }
    
    public void solve()
    {
        boolean sol=false;
        Graph<String, DefaultEdge> G = new SimpleGraph(DefaultEdge.class);  
        ArrayDeque<State> bfs = null;  
        try
        {        
            State temp = new State(light, 0);
            bfs = new ArrayDeque<State>();      

            bfs.add(temp);        
            while(!bfs.isEmpty())
            {            
                temp = bfs.pollFirst();                
                for(int j = temp.getLastPress()+1;j <= n_size*n_size;j++)
                {
                    State dummy = new State(temp.toggle(j), j);
                    if(!G.containsVertex(dummy.toString()))
                    {
                        
                        bfs.add(dummy);
                    }                        
                    
                    Graphs.addEdgeWithVertices(G, temp.toString(), dummy.toString());
                   
                    if(dummy.toString().equals(ans))
                    {
                        sol=true;
                        break;
                    }                             
                }   
                if(sol)
                    break;
                                
            }         
        }        
        catch(Exception ex)
        {
            sol = false;
            System.out.println(ex);            
        }        
        
        if(sol)
        {
            DijkstraShortestPath<String, DefaultEdge> D = new DijkstraShortestPath(G);  
            System.out.printf("%.0f move to turn off all light\n\n", D.getPath(str_start, ans).getWeight());
            List<String> a = D.getPath(str_start, ans).getVertexList();
            a.remove(0);
            for(String A : a)
            {                
                changeState(A);
                print(A);                            
            }
        }
        else
        {
            System.out.println("No Solution");
        }
    }
    
    public void changeState(String str_bits)
    {        
        ArrayList<int[]> change = new ArrayList();
        int temp=0;
        for(int i=0;i<n_size;i++)
        {   
            for(int j=0;j<n_size;j++, temp++)
            {                
                if(!((str_bits.charAt(temp)=='1'?true:false)==light[i][j]))
                {
                    light[i][j] = !light[i][j];                    
                    change.add(new int[]{i, j});
                }                                
            }            
        }
        boolean check;
        for(int[]n:change)
        {
            check = true;
            for(int[]m:change)
            {
                if(Math.abs(n[0]-m[0])+Math.abs(n[1]-m[1])!=1 && m!=n)
                {
                    check = false;
                    break;
                }
            }
            if(check)
            {
                System.out.printf("%s row %d column %d",light[n[0]][n[1]]==true?"turn on":"turn off", n[0], n[1]);
                break;
            }
        }
    }
    
    public void print(String bit)
    {         
        System.out.printf("\nBit String  = %s, decimal Id  = %d\n      ", bit, Integer.parseInt(bit, 2));        
        for(int i=0;i<n_size;i++)
        {
            System.out.printf(" | col %d ", i);
        }
        System.out.println("");
        for(int i=0;i<n_size;i++)
        {
            System.out.printf("row %d ", i);
            for(int j=0;j<n_size;j++)
            {
                System.out.printf(" |   %c   ", (light[i][j]==true)?'O':'x');
            }
            System.out.println("");
        }       
        System.out.println("");
    }
    
    public static void main(String argv[])
    {
        String run;
        do
        {
            new LightOut();
            System.out.println("Enter Y/y to calculate another LightsOut problem : ");
            Scanner in = new Scanner(System.in);
            run = in.nextLine();
        }while(run.equalsIgnoreCase("y"));        
    }
}