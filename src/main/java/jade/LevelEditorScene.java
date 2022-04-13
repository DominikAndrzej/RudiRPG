package jade;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        // position (x, y, z)    // color (r, g, b, a)
         0.5f, -0.5f, 0.0f,      1.0f, 0.9f, 0.0f, 1.0f, // 1 bottom right
        -0.5f,  0.5f, 0.0f,      1.0f, 1.0f, 0.9f, 1.0f, // 2 top left
         0.5f,  0.5f, 0.0f,      1.0f, 0.0f, 1.0f, 1.0f, // 3 top right
        -0.5f, -0.5f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f, // 4 bottom left
    };

    // IMPORTANT: must be in counter-clockwise order
    private int[] elementArray = {
            2, 1, 0, // top right triangle
            0, 1, 3 // bottom left triangle
    };

    private int vaoID, vboID, eboID;
    //vao - vertex array object
    //vbo - vertex buffer object
    //ebo - element buffer object

    public LevelEditorScene() {

    }

    @Override
    public void init() {
//        compileAndLinkShaders();
//        generateVaoVboEboForGPU();
        // Compile and link shaders

        // first load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass the shader to the GPU
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        // check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compilation failed :c");
            System.out.println(glGetShaderInfoLog(vertexID, length));
            assert false : "";
        }


        // for fragment

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        // check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment shader compilation failed :c");
            System.out.println(glGetShaderInfoLog(fragmentID, length));
            assert false : "";
        }

        // link shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        // check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int length = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tLinking shaders failed :c");
            System.out.println(glGetProgramInfoLog(shaderProgram, length));
            assert false : "";
        }

        // generate VAO, VBO, and EBO buffer objects, and send to GPU

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); //takes specific buffer (vertexBuffer) and give it to vboID

        // create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // add the vertex attributes pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    private void compileAndLinkShaders(){
//        // Compile and link shaders
//
//        // first load and compile the vertex shader
//        vertexID = glCreateShader(GL_VERTEX_SHADER);
//        // pass the shader to the GPU
//        glShaderSource(vertexID, vertexShaderSrc);
//        glCompileShader(vertexID);
//
//        // check for errors in compilation
//        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
//        if (success == GL_FALSE) {
//            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
//            System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compilation failed :c");
//            System.out.println(glGetShaderInfoLog(vertexID, length));
//            assert false : "";
//        }
//
//
//        // for fragment
//
//        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
//
//        glShaderSource(fragmentID, fragmentShaderSrc);
//        glCompileShader(fragmentID);
//
//        // check for errors in compilation
//        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
//        if (success == GL_FALSE) {
//            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
//            System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment shader compilation failed :c");
//            System.out.println(glGetShaderInfoLog(fragmentID, length));
//            assert false : "";
//        }
//
//        // link shaders and check for errors
//        shaderProgram = glCreateProgram();
//        glAttachShader(shaderProgram, vertexID);
//        glAttachShader(shaderProgram, fragmentID);
//        glLinkProgram(shaderProgram);
//
//        // check for linking errors
//        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
//        if (success == GL_FALSE) {
//            int length = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
//            System.out.println("ERROR: 'defaultShader.glsl'\n\tLinking shaders failed :c");
//            System.out.println(glGetProgramInfoLog(shaderProgram, length));
//            assert false : "";
//        }
    }

    private void generateVaoVboEboForGPU(){
//        // generate VAO, VBO, and EBO buffer objects, and send to GPU
//
//        vaoID = glGenVertexArrays();
//        glBindVertexArray(vaoID);
//
//        // create a float buffer of vertices
//        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
//        vertexBuffer.put(vertexArray).flip();
//
//        // create VBO upload the vertex buffer
//        vboID = glGenBuffers();
//        glBindBuffer(GL_ARRAY_BUFFER, vboID);
//        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); //takes specific buffer (vertexBuffer) and give it to vboID
//
//        // create the indices and upload
//        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
//        elementBuffer.put(elementArray).flip();
//
//        eboID = glGenBuffers();
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
//
//        // add the vertex atributes pointers
//        int positionsSize = 3;
//        int colorSize = 4;
//        int floatSizeBytes = 4;
//        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
//        glVertexAttribPointer(0, positionsSize, GL_FLAT, false, vertexSizeBytes, 0);
//        glEnableVertexAttribArray(0);
//
//        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
//        glEnableVertexAttribArray(1);

    }

    private void bindShader(){
        // bind shader program
        glUseProgram(shaderProgram);

        drawSquare();
    }

    private void drawSquare() {
        //bind the VAO that we're using
        glBindVertexArray(vaoID);

        //enable the vertex attrbute pointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
    }

    //możnaby zrobić klasę dla shaderów i w takiej klasie jak ta    //tylko tworzyć obiekt i już shader gotowy. Pytanie, czy w aplikacji
    //używa się kilku shaderów czy tylko jednego. Na pewno trzeba by tworzyć
    //rożne tablice wierzchołków. Nom, dla tablic wierzchołków zdecydowanie
    //przydałaby się klasa. Z tym, że taka klasa byłaby baaardzo skomplikowana

    private void unbindShader() {
        // unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);
    }

    @Override
    public void update(float dt) {
        bindShader();

        unbindShader();
    }
}
