package org.excellent.client.utils.render.font;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public final class FontData {
    @SerializedName("atlas")
    private AtlasData atlas;

    @SerializedName("metrics")
    private MetricsData metrics;

    @SerializedName("glyphs")
    private List<GlyphData> glyphs;

    @SerializedName("kerning")
    private List<KerningData> kernings;

    public AtlasData atlas() {
        return this.atlas;
    }

    public MetricsData metrics() {
        return this.metrics;
    }

    public List<GlyphData> glyphs() {
        return this.glyphs;
    }

    public List<KerningData> kernings() {
        return this.kernings;
    }

    public static final class AtlasData {

        @SerializedName("distanceRange")
        private float range;
        @SerializedName("width")
        private float width;
        @SerializedName("height")
        private float height;

        public float range() {
            return this.range;
        }

        public float width() {
            return this.width;
        }

        public float height() {
            return this.height;
        }

    }

    public static final class MetricsData {
        @SerializedName("lineHeight")
        private float lineHeight;
        @SerializedName("ascender")
        private float ascender;
        @SerializedName("descender")
        private float descender;

        public float lineHeight() {
            return this.lineHeight;
        }

        public float ascender() {
            return this.ascender;
        }

        public float descender() {
            return this.descender;
        }

        public float baselineHeight() {
            return this.lineHeight + this.descender;
        }

    }

    public static final class GlyphData {
        @SerializedName("unicode")
        private int unicode;
        @SerializedName("advance")
        private float advance;
        @SerializedName("planeBounds")
        private BoundsData planeBounds;
        @SerializedName("atlasBounds")
        private BoundsData atlasBounds;

        public int unicode() {
            return this.unicode;
        }

        public float advance() {
            return this.advance;
        }

        public BoundsData planeBounds() {
            return this.planeBounds;
        }

        public BoundsData atlasBounds() {
            return this.atlasBounds;
        }

    }

    public static final class BoundsData {
        @SerializedName("left")
        private float left;
        @SerializedName("top")
        private float top;
        @SerializedName("right")
        private float right;
        @SerializedName("bottom")
        private float bottom;

        public float left() {
            return this.left;
        }

        public float top() {
            return this.top;
        }

        public float right() {
            return this.right;
        }

        public float bottom() {
            return this.bottom;
        }

    }

    public static final class KerningData {

        @SerializedName("unicode1")
        private int leftChar;
        @SerializedName("unicode2")
        private int rightChar;
        @SerializedName("advance")
        private float advance;

        public int leftChar() {
            return this.leftChar;
        }

        public int rightChar() {
            return this.rightChar;
        }

        public float advance() {
            return this.advance;
        }

    }

}