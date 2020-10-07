using System;

namespace RoverOnMars
{
    class Program
    {
        static void Main(string[] args)
        {
            string[] thePlateau = Console.ReadLine().Split();
            int x = int.Parse(thePlateau[0]);
            int y = int.Parse(thePlateau[1]);

            Plateau plateau = new Plateau(x,y);

            string[] firstRoverInfo = Console.ReadLine().Split();
            Rover firstRover= new Rover(int.Parse(firstRoverInfo[0]),int.Parse(firstRoverInfo[1]),firstRoverInfo[2]);

            String firstCommand = Console.ReadLine();
            firstRover.processCommandString(firstCommand);

            string[] secondRoverInfo = Console.ReadLine().Split();
            Rover secondRover= new Rover(int.Parse(secondRoverInfo[0]),int.Parse(secondRoverInfo[1]),secondRoverInfo[2]);

            String secondCommand = Console.ReadLine();
            secondRover.processCommandString(secondCommand);

            firstRover.printInfo();
            secondRover.printInfo();
        }
    }
}