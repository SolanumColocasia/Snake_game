# Snake_game
This is a classic Snake game made using Java and Swing. Apples are generated randomly across the grid. As the snake moves and eats the apple, it grows bigger. The game tracks score and survival time. It includes a pause/play and restart functionality as well.

## Features 
- Smooth grid-based movement
- Randomly generated apples
- Snake length in increased after eating apple 
- Collision Detection
 - Self-collision
 - Wall collision 
- Home, Running, Pause and Game Over states
- Score tracking and survival time displayed
- Pause/Play and Reset buttons on the interface

## Project Structure 
### Entity class
Handles:
 - Grid unit size 
 - Entity X/Y positions
 - Entity dimensions
 - Collision detection 
 - Base class for Apple and Snake components 

### Snake class
Extends Entity class.
Contains:
- Head (Entity)
- ArrayList<Entity> body

Handles: 
- Snake movement and direction logic
- Growth mechanism

### GamePanel
Core logic:
- Rendering grid, snake and apples
- Generating apples 
- Updating movement
- Checking collision 
- Checking if apples eaten
- Handling GameStates 
- Updates score and survival time
- Key bindings (WASD | Arrow keys | Space bar | Enter)

### GameStates
Contains
- Home
- Pause
- Running
- Game Over

### GameWindow 
Main GUI Frame.
- Hosts the GamePanel
- Displays score, time, pause/play and restart buttons