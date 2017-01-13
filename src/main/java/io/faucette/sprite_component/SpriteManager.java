package io.faucette.sprite_component;


import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.ComponentManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;


public class SpriteManager extends ComponentManager {
    private int count;
    private List<List<Sprite>> layers;
    private List<Boolean> dirtyLayers;

    private List<Comparator<Sprite>> comparators;
    private Comparator<Sprite> defaultComparator = new DefaultComparator();


    public class DefaultComparator implements Comparator<Sprite> {
        @Override
        public int compare(Sprite a, Sprite b) {
            return new Integer(a.getZ()).compareTo(new Integer(b.getZ()));
        }
    }


    public SpriteManager() {
        super();

        layers = new ArrayList<>();
        dirtyLayers = new ArrayList<>();
        comparators = new ArrayList<>();
        count = 0;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    private SpriteManager setLayer(int layer, List<Sprite> layerArray) {
        if (layer >= layers.size()) {
            for (int i = layers.size(), il = layer; i < il; i++) {
                layers.add(new ArrayList<Sprite>());
            }
            layers.add(layerArray);
        } else {
            layers.set(layer, layerArray);
        }
        return this;
    }
    public SpriteManager setDirtyLayer(int layer) {
        if (layer >= dirtyLayers.size()) {
            for (int i = dirtyLayers.size(), il = layer; i < il; i++) {
                dirtyLayers.add(false);
            }
            dirtyLayers.add(true);
        } else {
            dirtyLayers.set(layer, true);
        }
        return this;
    }
    public SpriteManager setLayerComparators(int layer, Comparator<Sprite> comparator) {
        if (layer >= comparators.size()) {
            for (int i = comparators.size(), il = layer; i < il; i++) {
                comparators.add(defaultComparator);
            }
            comparators.add(comparator);
        } else {
            comparators.set(layer, comparator);
        }
        return this;
    }

    @Override
    public SpriteManager init() {
        sort();
        return this;
    }

    @Override
    public SpriteManager update() {
        sort();
        return this;
    }

    @Override
    public SpriteManager sort() {
        for (int i = 0, il = dirtyLayers.size(); i < il; i++) {
            if (dirtyLayers.get(i).booleanValue()) {
                dirtyLayers.set(i, false);
                Collections.sort(layers.get(i), comparators.get(i));
            }
        }
        return this;
    }

    private class SpriteIterator implements Iterator<Sprite> {
        private List<List<Sprite>> layers;
        private int layerIndex;
        private int index;
        private int count;


        public SpriteIterator(SpriteManager s) {
            layers = s.layers;
            layerIndex = 0;
            index = 0;
            count = s.count;
        }

        public boolean hasNext() {
            return count != 0;
        }
        public Sprite next() {
            if (count != 0) {
                List<Sprite> layer = layers.get(layerIndex);
                Sprite sprite;

                count -= 1;

                if (index >= layer.size()) {
                    index = 0;
                    layerIndex++;
                    layer = layers.get(layerIndex);
                }

                return layer.get(index++);
            } else {
                return null;
            }
        }
        public void remove() {}
    }

    @Override
    public Iterator<Sprite> iterator() {
        return new SpriteIterator(this);
    }

    @Override
    public boolean hasComponent(Component component) {
        Sprite sprite = (Sprite) component;

        try {
            List<Sprite> layer = layers.get(sprite.getLayer());
            return layer.contains(sprite);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
    @Override
    public <T extends Component> SpriteManager addComponent(T component) {
        Sprite sprite = (Sprite) component;
        int componentLayer = sprite.getLayer();
        List<Sprite> layer;

        try {
            layer = layers.get(componentLayer);
        } catch (IndexOutOfBoundsException e) {
            layer = new ArrayList<>();
            setLayer(componentLayer, layer);

            boolean needsLayerComparator = true;
            try {
                comparators.get(componentLayer);
                needsLayerComparator = false;
            } catch (IndexOutOfBoundsException e0) {}

            if (needsLayerComparator) {
                setLayerComparators(componentLayer, defaultComparator);
            }
        }

        setDirtyLayer(componentLayer);
        layer.add(sprite);
        count += 1;

        return this;
    }
    @Override
    public <T extends Component> SpriteManager removeComponent(T component) {
        Sprite sprite = (Sprite) component;
        int componentLayer = sprite.getLayer();

        try {
            List<Sprite> layer = layers.get(componentLayer);
            layer.remove(sprite);
            count -= 1;
        } catch (IndexOutOfBoundsException e) {}

        return this;
    }
}
