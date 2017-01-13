package io.faucette.sprite_component;


import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Scene;
import io.faucette.scene_graph.Entity;

import static org.junit.Assert.*;
import org.junit.*;


public class SpriteTest {
    @Test
    public void test() {
        Scene scene = new Scene();

        scene.addEntity(new Entity().addComponent(new Sprite()));
        scene.init();

        assertTrue(scene.hasComponentManager(SpriteManager.class));
    }

    @Test
    public void testLayers() {
        Scene scene = new Scene();

        for (int i = 0; i < 10; i++) {
            Sprite sprite = new Sprite();
            sprite.setLayer(10);
            scene.addEntity(new Entity().addComponent(sprite));
        }

        SpriteManager spriteManager = scene.getComponentManager(SpriteManager.class);

        final AtomicBoolean sortCalled = new AtomicBoolean(false);
        spriteManager.setLayerComparators(10, new Comparator<Sprite>() {
            @Override
            public int compare(Sprite a, Sprite b) {
                sortCalled.set(true);
                return 0;
            }
        });

        scene.init();

        scene.update();

        assertTrue(sortCalled.get());

        sortCalled.set(false);
        scene.update();

        assertFalse(sortCalled.get());
    }

    @Test
    public void testIterator() {
        Scene scene = new Scene();

        scene.addEntity(new Entity().addComponent(new Sprite().setLayer(0)));
        scene.addEntity(new Entity().addComponent(new Sprite().setLayer(1)));
        scene.addEntity(new Entity().addComponent(new Sprite().setLayer(2)));

        scene.init();

        Iterator<Sprite> it = scene.getComponentManager(SpriteManager.class).iterator();
        while (it.hasNext()) {
            it.next();
        }
    }
}
