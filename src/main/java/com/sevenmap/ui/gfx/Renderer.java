package com.sevenmap.ui.gfx;


import com.sevenmap.ui.Window;
import com.sevenmap.ui.elements.Camera;
import com.sevenmap.ui.elements.Item;
import com.sevenmap.ui.elements.RootNode;
import com.sevenmap.ui.math.Matrix4f;
import com.sevenmap.ui.elements.Node;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Renderer extends RootNode {
    private Shader shader;
    private Window window;

    /**
     * Create a new Renderer object.
     * @param shader the shader which will be applied on each render call.
     */
    public Renderer(Window window, Shader shader) {
        this.shader = shader;
        this.window = window;
    }

    /**
     * Render a specific element.
     * @param element
     */
    public void render(Item element, Camera camera) {
        // this needs some rework
        GL30.glBindVertexArray(element.getMesh().getVAO());
        GL20.glEnableVertexAttribArray(0); // shader data passing
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, element.getMesh().getIBO());
        
        if (element.getMesh().getMaterial() != null) {
            shader.setUniform("textureSample", 1);
            GL13.glActiveTexture(GL13.GL_TEXTURE0); 
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, element.getMesh().getMaterial().getTexID()); // bind texture
        } else {
            shader.setUniform("textureSample", 0);
        }
        
        shader.bind(); // bind before drawing 
        shader.setUniform("model", Matrix4f.transform(element.getPos(), element.getRot(), element.getScale()));
        shader.setUniform("view", Matrix4f.view(camera.getPos(), camera.getRot()));
        shader.setUniform("projection", window.getProjector());
        GL11.glDrawElements(GL11.GL_TRIANGLES, element.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);
        shader.unbind(); // unbind after drawing (ready for the next shader to be applied)
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    /**
     * Render all children.
     * @param camera camera on which the children have to be rendered
     */
    public void render(Camera camera) {
        children.forEach((Node node) -> {
            renderChildren(node, camera);
        });
    }

    /**
     * Render all children recursively.
     * @param node starting node
     * @param camera camera on which the children have to be rendered
     */
    private void renderChildren(Node node, Camera camera) {
        if (node.hasMesh()) {
            render((Item) node, camera);
        } else {
            node.getChildren().forEach((Node childnode) -> 
                renderChildren(childnode, camera));
        }
    }

    /**
     * Build all children meshes.
     */
    public void buildAll() {
        children.forEach((Node node) -> {
            buildChildren(node);
        });
    }

    /**
     * Build all children meshes recursively.
     * @param node starting node
     */
    public void buildChildren(Node node) {
        if (node.hasMesh()) {
            ((Item) node).getMesh().build();
        } else {
            node.getChildren().forEach((Node childnode) -> {
                buildChildren(childnode);
            });
        }
    }
}