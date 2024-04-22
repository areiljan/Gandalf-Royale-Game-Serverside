package ee.taltech.server.ai;

import com.esotericsoftware.minlog.Log;

import java.io.*;

public class Grid {
    public static int[][] grid;

    /**
     * Set grid.
     *
     * @param grid generated grid
     */
    public static void setGrid(int[][] grid) {
        Grid.grid = grid;
    }

    /**
     * Read grid from file.
     *
     * @return read grid as a 2d array
     */
    public static int[][] readGridFromFile() {
        int[][] grid = new int[1200][1200];

        try (InputStream inputStream = Grid.class.getResourceAsStream("/grid.txt");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int row = 0;

            while ((line = br.readLine()) != null && row < 1200) {
                for (int col = 0; col < 1200; col++) {
                    char c = line.charAt(col);
                    if (c == '1') {
                        grid[row][col] = 1;
                    } else if (c == '0') {
                        grid[row][col] = 0;
                    }
                }
                row++;
            }
            Log.info("Successfully read grid into array!");
        } catch (IOException e) {
            Log.info("Failed to read grid! Do you have 'grid.txt' in assets folder?");
        }

        return grid;
    }
}
