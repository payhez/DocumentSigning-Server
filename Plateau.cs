using System;

namespace RoverOnMars
{
    class Plateau
    {
        private int x,y;
        public Plateau(int x, int y){
            this.x=x;
            this.y=y;
        }

        public bool checkExceed(Rover rover){ // Checks if the rover goes out of the plateau
            if(rover.getPositionX()>this.x || rover.getPositionY()> this.y || rover.getPositionX()<0 || rover.getPositionY()<0){
                return false;
            }else{
                return true;
            }
        }
    }
}