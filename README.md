# Snake_game
This is a snake game. Apples are generated randomly across the grid. As the snake eats the apple, it grows bigger. 

### Entity class
Entity class supports entity characteristics such as collision detection, unit size, position and dimensions.

### Snake class
Snake class extends Entity class, it has an Entity object head and ArrayList<Entity> for body. It defines snake movement and direction at any point.

### GameWindow
Main class that interacts with the user. Apple generation, movement of snake, score and time, and the GUI frame are defined here.
