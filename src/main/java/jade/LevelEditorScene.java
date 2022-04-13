package jade;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import render.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {

    private Shader defaultShader;

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
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLink();

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

    @Override
    public void update(float dt) {
        defaultShader.use();
        //bind the VAO that we're using
        glBindVertexArray(vaoID);

        //enable the vertex attrbute pointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();
    }
}
