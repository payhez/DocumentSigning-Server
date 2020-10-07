using System;

namespace RoverOnMars
{
    class Rover
    {
        private int positionX, positionY;
        private String direction;

        public Rover(int positionX, int positionY, String direction)
        {
            this.positionX=positionX;
            this.positionY=positionY;
            this.direction=direction;
        }

        public int getPositionX(){
            return positionX;
        }
        public int getPositionY(){
            return positionY;
        }

        public String getDirection(){
            return direction;
        }
        public void printInfo(){
            Console.WriteLine(this.getPositionX() + " " + this.getPositionY() + " " + this.getDirection());
        }
        public void processCommandString(String commandString){
            foreach (char c in commandString)
            {
                this.move(c);
            }
        }
        private  void move(char command){
            switch (command)
            {
                case 'L':
                    if(this.direction == "N"){
                        this.direction = "W";
                    }else if(this.direction =="W"){
                        this.direction="S";
                    }else if(this.direction =="S"){
                        this.direction="E";
                    }else if(this.direction =="E"){
                        this.direction="N";
                    }
                break;

                case 'R':
                    if(this.direction == "N"){
                        this.direction = "E";
                    }else if(this.direction =="W"){
                        this.direction="N";
                    }else if(this.direction =="S"){
                        this.direction="W";
                    }else if(this.direction =="E"){
                        this.direction="S";
                    }
                break;
                
                case 'M':
                    if(this.direction == "N"){
                        this.positionY++;
                    }else if(this.direction =="W"){
                        this.positionX--;
                    }else if(this.direction =="S"){
                        this.positionY--;
                    }else if(this.direction =="E"){
                        this.positionX++;
                    }
                break;
                default:
                break;
            }
        }
    }
}