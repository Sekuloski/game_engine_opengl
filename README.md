<h3 align="center">Game engine using OpenGL</h3>

  <p align="center">
    This is a simple project made using the Lightweight Java Game Library 2.
    <br />
    <a href="https://github.com/sekuloski/game_engine_opengl/tree/computer_graphics_course">View Demo</a>
    ·
    <a href="https://github.com/sekuloski/game_engine_opengl/tree/computer_graphics_course/issues">Report Bug</a>
    ·
    <a href="https://github.com/sekuloski/game_engine_opengl/tree/computer_graphics_course/issues">Request Feature</a>
  </p>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

It represents a game engine that engines like Unity and Unreal use as a backbone for their engine. Of course, it is nowhere near as complex and complete as them,
but it is a good project to learn how OpenGL works and how games were made before those engines were made.



### Built With

* [Lwjgl 2](http://legacy.lwjgl.org/)

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

All the prerequisites are already installed and are located in the [libs](libs) folder


### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/Sekuloski/game_engine_opengl/tree/computer_graphics_course
   ```
2. Add the [jars](libs/jars) and [natives](libs/natives) libraries in the project structure. This is different for different editing programs. 

    For intellij go to *File*, *Project Structure* and then *Libraries* and add the folders.

<!-- USAGE EXAMPLES -->
## Usage

To run the project, simply run the main method of the *[MainLoop](src/Engine_Tester/MainLoop.java)* class and the demo project will run.
In the project there a few files that store the list of entities, models and lights, and they are already filled with some data. To alter them, there is an if segment in the main method in the while loop, that is currently commented. After uncommenting it and running the
main method, by pressing the right mouse button you can spawn the currently set *entityModel* (to set the entityModel, simply go the top of the class and
write the name of the model which can be found in the [models](src/txt/models.txt) file). After spawning the entity
you can move it by moving the player using the WASD keys and the entity will always be at the center of the screen.
To place the object just place the left mouse button. This entity will then be stored in the [entities](src/txt/entities.txt) file which stores all the entities.

To quit the game, press the del key.

There are plenty of other functionalities available. They are all in the *[MainLoop](src/Engine_Tester/MainLoop.java)* class. If you want to make your own map, go to the contributing part of this
readme file.


<!-- ROADMAP -->
## Roadmap

This project has a lot of features that need to be implemented. Here is a list of some of them.

- Collisions
- Point shadows
- Water light reflection
- Bloom and glow
- God rays

See the [open issues](https://github.com/sekuloski/game_engine_opengl/tree/computer_graphics_course/issues) for a full list of proposed features (and known issues).

<!-- CONTRIBUTING -->
## Contributing

If you have a suggestion that would make this better or want to create a map of your own, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Entities, models and lights are stored in the [txt](src/txt) folder.
Textures are stored in the [res](resources/res) folder.
Objects are stored in the [obj](resources/obj) folder. Make sure to export obj files with triangulated faces otherwise the loader will fail to open them.
The terrain is created using a heightmap which you can modify or create a new one.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- CONTACT -->
## Contact

Bojan Sekuloski - [@twitter_handle](https://twitter.com/b_sekuloski) - sekuloski.bojan18@gmail.com