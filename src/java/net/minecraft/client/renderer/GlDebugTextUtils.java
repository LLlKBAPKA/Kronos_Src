package net.minecraft.client.renderer;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.mojang.blaze3d.platform.GLX;
import net.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.Config;
import net.optifine.GlErrors;
import net.optifine.util.ArrayUtils;
import net.optifine.util.StrUtils;
import net.optifine.util.TimedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.KHRDebug;

public class GlDebugTextUtils
{
    private static final Logger LOGGER = LogManager.getLogger();
    protected static final ByteBuffer BYTE_BUFFER = GLAllocation.createDirectByteBuffer(64);
    protected static final FloatBuffer FLOAT_BUFFER = BYTE_BUFFER.asFloatBuffer();
    protected static final IntBuffer INT_BUFFER = BYTE_BUFFER.asIntBuffer();
    private static final Joiner NEWLINE_JOINER = Joiner.on('\n');
    private static final Joiner STATEMENT_JOINER = Joiner.on("; ");
    private static final Map<Integer, String> GL_CONSTANT_NAMES = Maps.newHashMap();
    private static final List<Integer> DEBUG_LEVELS = ImmutableList.of(37190, 37191, 37192, 33387);
    private static final List<Integer> DEBUG_LEVELS_ARB = ImmutableList.of(37190, 37191, 37192);
    private static final Map<String, List<String>> SAVED_STATES = Maps.newHashMap();
    private static int[] ignoredErrors = makeIgnoredErrors();

    private static int[] makeIgnoredErrors()
    {
        String s = System.getProperty("gl.ignore.errors");

        if (s == null)
        {
            return new int[0];
        }
        else
        {
            String[] astring = Config.tokenize(s, ",");
            int[] aint = new int[0];

            for (int i = 0; i < astring.length; ++i)
            {
                String s1 = astring[i].trim();
                int j = s1.startsWith("0x") ? Config.parseHexInt(s1, -1) : Config.parseInt(s1, -1);

                if (j < 0)
                {
                    Config.warn("Invalid error id: " + s1);
                }
                else
                {
                    Config.log("Ignore OpenGL error: " + j);
                    aint = ArrayUtils.addIntToArray(aint, j);
                }
            }

            return aint;
        }
    }

    private static String getFallbackString(int p_209245_0_)
    {
        return "Unknown (0x" + Integer.toHexString(p_209245_0_).toUpperCase() + ")";
    }

    private static String getSource(int p_209242_0_)
    {
        switch (p_209242_0_)
        {
            case 33350:
                return "API";

            case 33351:
                return "WINDOW SYSTEM";

            case 33352:
                return "SHADER COMPILER";

            case 33353:
                return "THIRD PARTY";

            case 33354:
                return "APPLICATION";

            case 33355:
                return "OTHER";

            default:
                return getFallbackString(p_209242_0_);
        }
    }

    private static String getType(int p_209248_0_)
    {
        switch (p_209248_0_)
        {
            case 33356:
                return "ERROR";

            case 33357:
                return "DEPRECATED BEHAVIOR";

            case 33358:
                return "UNDEFINED BEHAVIOR";

            case 33359:
                return "PORTABILITY";

            case 33360:
                return "PERFORMANCE";

            case 33361:
                return "OTHER";

            case 33384:
                return "MARKER";

            default:
                return getFallbackString(p_209248_0_);
        }
    }

    private static String getSeverity(int p_209246_0_)
    {
        switch (p_209246_0_)
        {
            case 33387:
                return "NOTIFICATION";

            case 37190:
                return "HIGH";

            case 37191:
                return "MEDIUM";

            case 37192:
                return "LOW";

            default:
                return getFallbackString(p_209246_0_);
        }
    }

    private static void logDebugMessage(int source, int type, int id, int severity, int messageLength, long message, long p_209244_7_)
    {
        if (type != 33385 && type != 33386)
        {
            if (!ArrayUtils.contains(ignoredErrors, id))
            {
                if (!Config.isShaders() || source != 33352)
                {
                    Minecraft minecraft = Minecraft.getInstance();

                    if (minecraft == null || minecraft.getMainWindow() == null || !minecraft.getMainWindow().isClosed())
                    {
                        if (GlErrors.isEnabled(id))
                        {
                            String s = getSource(source);
                            String s1 = getType(type);
                            String s2 = getSeverity(severity);
                            String s3 = GLDebugMessageCallback.getMessage(messageLength, message);
                            s3 = StrUtils.trim(s3, " \n\r\t");
                            String s4 = String.format("OpenGL %s %s: %s (%s)", s, s1, id, s3);
                            Exception exception = new Exception("Stack trace");
                            StackTraceElement[] astacktraceelement = exception.getStackTrace();
                            StackTraceElement[] astacktraceelement1 = astacktraceelement.length > 2 ? Arrays.copyOfRange(astacktraceelement, 2, astacktraceelement.length) : astacktraceelement;
                            exception.setStackTrace(astacktraceelement1);

                            if (type == 33356)
                            {
                                LOGGER.error(s4, (Throwable)exception);
                            }
                            else
                            {
                                LOGGER.info(s4, (Throwable)exception);
                            }

                            if (Config.isShowGlErrors() && TimedEvent.isActive("ShowGlErrorDebug", 10000L))
                            {
                                String s5 = Config.getGlErrorString(id);

                                if (id == 0 || Config.equals(s5, "Unknown"))
                                {
                                    s5 = s3;
                                }

                                String s6 = I18n.format("of.message.openglError", id, s5);
                                Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(s6));
                            }
                        }
                    }
                }
            }
        }
    }

    private static void registerGlConstantName(int value, String name)
    {
        GL_CONSTANT_NAMES.merge(value, name, (p_lambda$registerGlConstantName$0_0_, p_lambda$registerGlConstantName$0_1_) ->
        {
            return p_lambda$registerGlConstantName$0_0_ + "/" + p_lambda$registerGlConstantName$0_1_;
        });
    }

    public static void setDebugVerbosity(int debugVerbosity, boolean synchronous)
    {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);

        if (debugVerbosity > 0)
        {
            GLCapabilities glcapabilities = GL.getCapabilities();

            if (glcapabilities.GL_KHR_debug)
            {
                GL11.glEnable(37600);

                if (synchronous)
                {
                    GL11.glEnable(33346);
                }

                for (int i = 0; i < DEBUG_LEVELS.size(); ++i)
                {
                    boolean flag = i < debugVerbosity;
                    KHRDebug.glDebugMessageControl(4352, 4352, DEBUG_LEVELS.get(i), (int[])null, flag);
                }

                KHRDebug.glDebugMessageCallback(GLX.make(GLDebugMessageCallback.create(GlDebugTextUtils::logDebugMessage), LWJGLMemoryUntracker::untrack), 0L);
            }
            else if (glcapabilities.GL_ARB_debug_output)
            {
                if (synchronous)
                {
                    GL11.glEnable(33346);
                }

                for (int j = 0; j < DEBUG_LEVELS_ARB.size(); ++j)
                {
                    boolean flag1 = j < debugVerbosity;
                    ARBDebugOutput.glDebugMessageControlARB(4352, 4352, DEBUG_LEVELS_ARB.get(j), (int[])null, flag1);
                }

                ARBDebugOutput.glDebugMessageCallbackARB(GLX.make(GLDebugMessageARBCallback.create(GlDebugTextUtils::logDebugMessage), LWJGLMemoryUntracker::untrack), 0L);
            }
        }
    }

    static
    {
        registerGlConstantName(256, "GL11.GL_ACCUM");
        registerGlConstantName(257, "GL11.GL_LOAD");
        registerGlConstantName(258, "GL11.GL_RETURN");
        registerGlConstantName(259, "GL11.GL_MULT");
        registerGlConstantName(260, "GL11.GL_ADD");
        registerGlConstantName(512, "GL11.GL_NEVER");
        registerGlConstantName(513, "GL11.GL_LESS");
        registerGlConstantName(514, "GL11.GL_EQUAL");
        registerGlConstantName(515, "GL11.GL_LEQUAL");
        registerGlConstantName(516, "GL11.GL_GREATER");
        registerGlConstantName(517, "GL11.GL_NOTEQUAL");
        registerGlConstantName(518, "GL11.GL_GEQUAL");
        registerGlConstantName(519, "GL11.GL_ALWAYS");
        registerGlConstantName(0, "GL11.GL_POINTS");
        registerGlConstantName(1, "GL11.GL_LINES");
        registerGlConstantName(2, "GL11.GL_LINE_LOOP");
        registerGlConstantName(3, "GL11.GL_LINE_STRIP");
        registerGlConstantName(4, "GL11.GL_TRIANGLES");
        registerGlConstantName(5, "GL11.GL_TRIANGLE_STRIP");
        registerGlConstantName(6, "GL11.GL_TRIANGLE_FAN");
        registerGlConstantName(7, "GL11.GL_QUADS");
        registerGlConstantName(8, "GL11.GL_QUAD_STRIP");
        registerGlConstantName(9, "GL11.GL_POLYGON");
        registerGlConstantName(0, "GL11.GL_ZERO");
        registerGlConstantName(1, "GL11.GL_ONE");
        registerGlConstantName(768, "GL11.GL_SRC_COLOR");
        registerGlConstantName(769, "GL11.GL_ONE_MINUS_SRC_COLOR");
        registerGlConstantName(770, "GL11.GL_SRC_ALPHA");
        registerGlConstantName(771, "GL11.GL_ONE_MINUS_SRC_ALPHA");
        registerGlConstantName(772, "GL11.GL_DST_ALPHA");
        registerGlConstantName(773, "GL11.GL_ONE_MINUS_DST_ALPHA");
        registerGlConstantName(774, "GL11.GL_DST_COLOR");
        registerGlConstantName(775, "GL11.GL_ONE_MINUS_DST_COLOR");
        registerGlConstantName(776, "GL11.GL_SRC_ALPHA_SATURATE");
        registerGlConstantName(32769, "GL14.GL_CONSTANT_COLOR");
        registerGlConstantName(32770, "GL14.GL_ONE_MINUS_CONSTANT_COLOR");
        registerGlConstantName(32771, "GL14.GL_CONSTANT_ALPHA");
        registerGlConstantName(32772, "GL14.GL_ONE_MINUS_CONSTANT_ALPHA");
        registerGlConstantName(1, "GL11.GL_TRUE");
        registerGlConstantName(0, "GL11.GL_FALSE");
        registerGlConstantName(12288, "GL11.GL_CLIP_PLANE0");
        registerGlConstantName(12289, "GL11.GL_CLIP_PLANE1");
        registerGlConstantName(12290, "GL11.GL_CLIP_PLANE2");
        registerGlConstantName(12291, "GL11.GL_CLIP_PLANE3");
        registerGlConstantName(12292, "GL11.GL_CLIP_PLANE4");
        registerGlConstantName(12293, "GL11.GL_CLIP_PLANE5");
        registerGlConstantName(5120, "GL11.GL_BYTE");
        registerGlConstantName(5121, "GL11.GL_UNSIGNED_BYTE");
        registerGlConstantName(5122, "GL11.GL_SHORT");
        registerGlConstantName(5123, "GL11.GL_UNSIGNED_SHORT");
        registerGlConstantName(5124, "GL11.GL_INT");
        registerGlConstantName(5125, "GL11.GL_UNSIGNED_INT");
        registerGlConstantName(5126, "GL11.GL_FLOAT");
        registerGlConstantName(5127, "GL11.GL_2_BYTES");
        registerGlConstantName(5128, "GL11.GL_3_BYTES");
        registerGlConstantName(5129, "GL11.GL_4_BYTES");
        registerGlConstantName(5130, "GL11.GL_DOUBLE");
        registerGlConstantName(0, "GL11.GL_NONE");
        registerGlConstantName(1024, "GL11.GL_FRONT_LEFT");
        registerGlConstantName(1025, "GL11.GL_FRONT_RIGHT");
        registerGlConstantName(1026, "GL11.GL_BACK_LEFT");
        registerGlConstantName(1027, "GL11.GL_BACK_RIGHT");
        registerGlConstantName(1028, "GL11.GL_FRONT");
        registerGlConstantName(1029, "GL11.GL_BACK");
        registerGlConstantName(1030, "GL11.GL_LEFT");
        registerGlConstantName(1031, "GL11.GL_RIGHT");
        registerGlConstantName(1032, "GL11.GL_FRONT_AND_BACK");
        registerGlConstantName(1033, "GL11.GL_AUX0");
        registerGlConstantName(1034, "GL11.GL_AUX1");
        registerGlConstantName(1035, "GL11.GL_AUX2");
        registerGlConstantName(1036, "GL11.GL_AUX3");
        registerGlConstantName(0, "GL11.GL_NO_ERROR");
        registerGlConstantName(1280, "GL11.GL_INVALID_ENUM");
        registerGlConstantName(1281, "GL11.GL_INVALID_VALUE");
        registerGlConstantName(1282, "GL11.GL_INVALID_OPERATION");
        registerGlConstantName(1283, "GL11.GL_STACK_OVERFLOW");
        registerGlConstantName(1284, "GL11.GL_STACK_UNDERFLOW");
        registerGlConstantName(1285, "GL11.GL_OUT_OF_MEMORY");
        registerGlConstantName(1536, "GL11.GL_2D");
        registerGlConstantName(1537, "GL11.GL_3D");
        registerGlConstantName(1538, "GL11.GL_3D_COLOR");
        registerGlConstantName(1539, "GL11.GL_3D_COLOR_TEXTURE");
        registerGlConstantName(1540, "GL11.GL_4D_COLOR_TEXTURE");
        registerGlConstantName(1792, "GL11.GL_PASS_THROUGH_TOKEN");
        registerGlConstantName(1793, "GL11.GL_POINT_TOKEN");
        registerGlConstantName(1794, "GL11.GL_LINE_TOKEN");
        registerGlConstantName(1795, "GL11.GL_POLYGON_TOKEN");
        registerGlConstantName(1796, "GL11.GL_BITMAP_TOKEN");
        registerGlConstantName(1797, "GL11.GL_DRAW_PIXEL_TOKEN");
        registerGlConstantName(1798, "GL11.GL_COPY_PIXEL_TOKEN");
        registerGlConstantName(1799, "GL11.GL_LINE_RESET_TOKEN");
        registerGlConstantName(2048, "GL11.GL_EXP");
        registerGlConstantName(2049, "GL11.GL_EXP2");
        registerGlConstantName(2304, "GL11.GL_CW");
        registerGlConstantName(2305, "GL11.GL_CCW");
        registerGlConstantName(2560, "GL11.GL_COEFF");
        registerGlConstantName(2561, "GL11.GL_ORDER");
        registerGlConstantName(2562, "GL11.GL_DOMAIN");
        registerGlConstantName(2816, "GL11.GL_CURRENT_COLOR");
        registerGlConstantName(2817, "GL11.GL_CURRENT_INDEX");
        registerGlConstantName(2818, "GL11.GL_CURRENT_NORMAL");
        registerGlConstantName(2819, "GL11.GL_CURRENT_TEXTURE_COORDS");
        registerGlConstantName(2820, "GL11.GL_CURRENT_RASTER_COLOR");
        registerGlConstantName(2821, "GL11.GL_CURRENT_RASTER_INDEX");
        registerGlConstantName(2822, "GL11.GL_CURRENT_RASTER_TEXTURE_COORDS");
        registerGlConstantName(2823, "GL11.GL_CURRENT_RASTER_POSITION");
        registerGlConstantName(2824, "GL11.GL_CURRENT_RASTER_POSITION_VALID");
        registerGlConstantName(2825, "GL11.GL_CURRENT_RASTER_DISTANCE");
        registerGlConstantName(2832, "GL11.GL_POINT_SMOOTH");
        registerGlConstantName(2833, "GL11.GL_POINT_SIZE");
        registerGlConstantName(2834, "GL11.GL_POINT_SIZE_RANGE");
        registerGlConstantName(2835, "GL11.GL_POINT_SIZE_GRANULARITY");
        registerGlConstantName(2848, "GL11.GL_LINE_SMOOTH");
        registerGlConstantName(2849, "GL11.GL_LINE_WIDTH");
        registerGlConstantName(2850, "GL11.GL_LINE_WIDTH_RANGE");
        registerGlConstantName(2851, "GL11.GL_LINE_WIDTH_GRANULARITY");
        registerGlConstantName(2852, "GL11.GL_LINE_STIPPLE");
        registerGlConstantName(2853, "GL11.GL_LINE_STIPPLE_PATTERN");
        registerGlConstantName(2854, "GL11.GL_LINE_STIPPLE_REPEAT");
        registerGlConstantName(2864, "GL11.GL_LIST_MODE");
        registerGlConstantName(2865, "GL11.GL_MAX_LIST_NESTING");
        registerGlConstantName(2866, "GL11.GL_LIST_BASE");
        registerGlConstantName(2867, "GL11.GL_LIST_INDEX");
        registerGlConstantName(2880, "GL11.GL_POLYGON_MODE");
        registerGlConstantName(2881, "GL11.GL_POLYGON_SMOOTH");
        registerGlConstantName(2882, "GL11.GL_POLYGON_STIPPLE");
        registerGlConstantName(2883, "GL11.GL_EDGE_FLAG");
        registerGlConstantName(2884, "GL11.GL_CULL_FACE");
        registerGlConstantName(2885, "GL11.GL_CULL_FACE_MODE");
        registerGlConstantName(2886, "GL11.GL_FRONT_FACE");
        registerGlConstantName(2896, "GL11.GL_LIGHTING");
        registerGlConstantName(2897, "GL11.GL_LIGHT_MODEL_LOCAL_VIEWER");
        registerGlConstantName(2898, "GL11.GL_LIGHT_MODEL_TWO_SIDE");
        registerGlConstantName(2899, "GL11.GL_LIGHT_MODEL_AMBIENT");
        registerGlConstantName(2900, "GL11.GL_SHADE_MODEL");
        registerGlConstantName(2901, "GL11.GL_COLOR_MATERIAL_FACE");
        registerGlConstantName(2902, "GL11.GL_COLOR_MATERIAL_PARAMETER");
        registerGlConstantName(2903, "GL11.GL_COLOR_MATERIAL");
        registerGlConstantName(2912, "GL11.GL_FOG");
        registerGlConstantName(2913, "GL11.GL_FOG_INDEX");
        registerGlConstantName(2914, "GL11.GL_FOG_DENSITY");
        registerGlConstantName(2915, "GL11.GL_FOG_START");
        registerGlConstantName(2916, "GL11.GL_FOG_END");
        registerGlConstantName(2917, "GL11.GL_FOG_MODE");
        registerGlConstantName(2918, "GL11.GL_FOG_COLOR");
        registerGlConstantName(2928, "GL11.GL_DEPTH_RANGE");
        registerGlConstantName(2929, "GL11.GL_DEPTH_TEST");
        registerGlConstantName(2930, "GL11.GL_DEPTH_WRITEMASK");
        registerGlConstantName(2931, "GL11.GL_DEPTH_CLEAR_VALUE");
        registerGlConstantName(2932, "GL11.GL_DEPTH_FUNC");
        registerGlConstantName(2944, "GL11.GL_ACCUM_CLEAR_VALUE");
        registerGlConstantName(2960, "GL11.GL_STENCIL_TEST");
        registerGlConstantName(2961, "GL11.GL_STENCIL_CLEAR_VALUE");
        registerGlConstantName(2962, "GL11.GL_STENCIL_FUNC");
        registerGlConstantName(2963, "GL11.GL_STENCIL_VALUE_MASK");
        registerGlConstantName(2964, "GL11.GL_STENCIL_FAIL");
        registerGlConstantName(2965, "GL11.GL_STENCIL_PASS_DEPTH_FAIL");
        registerGlConstantName(2966, "GL11.GL_STENCIL_PASS_DEPTH_PASS");
        registerGlConstantName(2967, "GL11.GL_STENCIL_REF");
        registerGlConstantName(2968, "GL11.GL_STENCIL_WRITEMASK");
        registerGlConstantName(2976, "GL11.GL_MATRIX_MODE");
        registerGlConstantName(2977, "GL11.GL_NORMALIZE");
        registerGlConstantName(2978, "GL11.GL_VIEWPORT");
        registerGlConstantName(2979, "GL11.GL_MODELVIEW_STACK_DEPTH");
        registerGlConstantName(2980, "GL11.GL_PROJECTION_STACK_DEPTH");
        registerGlConstantName(2981, "GL11.GL_TEXTURE_STACK_DEPTH");
        registerGlConstantName(2982, "GL11.GL_MODELVIEW_MATRIX");
        registerGlConstantName(2983, "GL11.GL_PROJECTION_MATRIX");
        registerGlConstantName(2984, "GL11.GL_TEXTURE_MATRIX");
        registerGlConstantName(2992, "GL11.GL_ATTRIB_STACK_DEPTH");
        registerGlConstantName(2993, "GL11.GL_CLIENT_ATTRIB_STACK_DEPTH");
        registerGlConstantName(3008, "GL11.GL_ALPHA_TEST");
        registerGlConstantName(3009, "GL11.GL_ALPHA_TEST_FUNC");
        registerGlConstantName(3010, "GL11.GL_ALPHA_TEST_REF");
        registerGlConstantName(3024, "GL11.GL_DITHER");
        registerGlConstantName(3040, "GL11.GL_BLEND_DST");
        registerGlConstantName(3041, "GL11.GL_BLEND_SRC");
        registerGlConstantName(3042, "GL11.GL_BLEND");
        registerGlConstantName(3056, "GL11.GL_LOGIC_OP_MODE");
        registerGlConstantName(3057, "GL11.GL_INDEX_LOGIC_OP");
        registerGlConstantName(3058, "GL11.GL_COLOR_LOGIC_OP");
        registerGlConstantName(3072, "GL11.GL_AUX_BUFFERS");
        registerGlConstantName(3073, "GL11.GL_DRAW_BUFFER");
        registerGlConstantName(3074, "GL11.GL_READ_BUFFER");
        registerGlConstantName(3088, "GL11.GL_SCISSOR_BOX");
        registerGlConstantName(3089, "GL11.GL_SCISSOR_TEST");
        registerGlConstantName(3104, "GL11.GL_INDEX_CLEAR_VALUE");
        registerGlConstantName(3105, "GL11.GL_INDEX_WRITEMASK");
        registerGlConstantName(3106, "GL11.GL_COLOR_CLEAR_VALUE");
        registerGlConstantName(3107, "GL11.GL_COLOR_WRITEMASK");
        registerGlConstantName(3120, "GL11.GL_INDEX_MODE");
        registerGlConstantName(3121, "GL11.GL_RGBA_MODE");
        registerGlConstantName(3122, "GL11.GL_DOUBLEBUFFER");
        registerGlConstantName(3123, "GL11.GL_STEREO");
        registerGlConstantName(3136, "GL11.GL_RENDER_MODE");
        registerGlConstantName(3152, "GL11.GL_PERSPECTIVE_CORRECTION_HINT");
        registerGlConstantName(3153, "GL11.GL_POINT_SMOOTH_HINT");
        registerGlConstantName(3154, "GL11.GL_LINE_SMOOTH_HINT");
        registerGlConstantName(3155, "GL11.GL_POLYGON_SMOOTH_HINT");
        registerGlConstantName(3156, "GL11.GL_FOG_HINT");
        registerGlConstantName(3168, "GL11.GL_TEXTURE_GEN_S");
        registerGlConstantName(3169, "GL11.GL_TEXTURE_GEN_T");
        registerGlConstantName(3170, "GL11.GL_TEXTURE_GEN_R");
        registerGlConstantName(3171, "GL11.GL_TEXTURE_GEN_Q");
        registerGlConstantName(3184, "GL11.GL_PIXEL_MAP_I_TO_I");
        registerGlConstantName(3185, "GL11.GL_PIXEL_MAP_S_TO_S");
        registerGlConstantName(3186, "GL11.GL_PIXEL_MAP_I_TO_R");
        registerGlConstantName(3187, "GL11.GL_PIXEL_MAP_I_TO_G");
        registerGlConstantName(3188, "GL11.GL_PIXEL_MAP_I_TO_B");
        registerGlConstantName(3189, "GL11.GL_PIXEL_MAP_I_TO_A");
        registerGlConstantName(3190, "GL11.GL_PIXEL_MAP_R_TO_R");
        registerGlConstantName(3191, "GL11.GL_PIXEL_MAP_G_TO_G");
        registerGlConstantName(3192, "GL11.GL_PIXEL_MAP_B_TO_B");
        registerGlConstantName(3193, "GL11.GL_PIXEL_MAP_A_TO_A");
        registerGlConstantName(3248, "GL11.GL_PIXEL_MAP_I_TO_I_SIZE");
        registerGlConstantName(3249, "GL11.GL_PIXEL_MAP_S_TO_S_SIZE");
        registerGlConstantName(3250, "GL11.GL_PIXEL_MAP_I_TO_R_SIZE");
        registerGlConstantName(3251, "GL11.GL_PIXEL_MAP_I_TO_G_SIZE");
        registerGlConstantName(3252, "GL11.GL_PIXEL_MAP_I_TO_B_SIZE");
        registerGlConstantName(3253, "GL11.GL_PIXEL_MAP_I_TO_A_SIZE");
        registerGlConstantName(3254, "GL11.GL_PIXEL_MAP_R_TO_R_SIZE");
        registerGlConstantName(3255, "GL11.GL_PIXEL_MAP_G_TO_G_SIZE");
        registerGlConstantName(3256, "GL11.GL_PIXEL_MAP_B_TO_B_SIZE");
        registerGlConstantName(3257, "GL11.GL_PIXEL_MAP_A_TO_A_SIZE");
        registerGlConstantName(3312, "GL11.GL_UNPACK_SWAP_BYTES");
        registerGlConstantName(3313, "GL11.GL_UNPACK_LSB_FIRST");
        registerGlConstantName(3314, "GL11.GL_UNPACK_ROW_LENGTH");
        registerGlConstantName(3315, "GL11.GL_UNPACK_SKIP_ROWS");
        registerGlConstantName(3316, "GL11.GL_UNPACK_SKIP_PIXELS");
        registerGlConstantName(3317, "GL11.GL_UNPACK_ALIGNMENT");
        registerGlConstantName(3328, "GL11.GL_PACK_SWAP_BYTES");
        registerGlConstantName(3329, "GL11.GL_PACK_LSB_FIRST");
        registerGlConstantName(3330, "GL11.GL_PACK_ROW_LENGTH");
        registerGlConstantName(3331, "GL11.GL_PACK_SKIP_ROWS");
        registerGlConstantName(3332, "GL11.GL_PACK_SKIP_PIXELS");
        registerGlConstantName(3333, "GL11.GL_PACK_ALIGNMENT");
        registerGlConstantName(3344, "GL11.GL_MAP_COLOR");
        registerGlConstantName(3345, "GL11.GL_MAP_STENCIL");
        registerGlConstantName(3346, "GL11.GL_INDEX_SHIFT");
        registerGlConstantName(3347, "GL11.GL_INDEX_OFFSET");
        registerGlConstantName(3348, "GL11.GL_RED_SCALE");
        registerGlConstantName(3349, "GL11.GL_RED_BIAS");
        registerGlConstantName(3350, "GL11.GL_ZOOM_X");
        registerGlConstantName(3351, "GL11.GL_ZOOM_Y");
        registerGlConstantName(3352, "GL11.GL_GREEN_SCALE");
        registerGlConstantName(3353, "GL11.GL_GREEN_BIAS");
        registerGlConstantName(3354, "GL11.GL_BLUE_SCALE");
        registerGlConstantName(3355, "GL11.GL_BLUE_BIAS");
        registerGlConstantName(3356, "GL11.GL_ALPHA_SCALE");
        registerGlConstantName(3357, "GL11.GL_ALPHA_BIAS");
        registerGlConstantName(3358, "GL11.GL_DEPTH_SCALE");
        registerGlConstantName(3359, "GL11.GL_DEPTH_BIAS");
        registerGlConstantName(3376, "GL11.GL_MAX_EVAL_ORDER");
        registerGlConstantName(3377, "GL11.GL_MAX_LIGHTS");
        registerGlConstantName(3378, "GL11.GL_MAX_CLIP_PLANES");
        registerGlConstantName(3379, "GL11.GL_MAX_TEXTURE_SIZE");
        registerGlConstantName(3380, "GL11.GL_MAX_PIXEL_MAP_TABLE");
        registerGlConstantName(3381, "GL11.GL_MAX_ATTRIB_STACK_DEPTH");
        registerGlConstantName(3382, "GL11.GL_MAX_MODELVIEW_STACK_DEPTH");
        registerGlConstantName(3383, "GL11.GL_MAX_NAME_STACK_DEPTH");
        registerGlConstantName(3384, "GL11.GL_MAX_PROJECTION_STACK_DEPTH");
        registerGlConstantName(3385, "GL11.GL_MAX_TEXTURE_STACK_DEPTH");
        registerGlConstantName(3386, "GL11.GL_MAX_VIEWPORT_DIMS");
        registerGlConstantName(3387, "GL11.GL_MAX_CLIENT_ATTRIB_STACK_DEPTH");
        registerGlConstantName(3408, "GL11.GL_SUBPIXEL_BITS");
        registerGlConstantName(3409, "GL11.GL_INDEX_BITS");
        registerGlConstantName(3410, "GL11.GL_RED_BITS");
        registerGlConstantName(3411, "GL11.GL_GREEN_BITS");
        registerGlConstantName(3412, "GL11.GL_BLUE_BITS");
        registerGlConstantName(3413, "GL11.GL_ALPHA_BITS");
        registerGlConstantName(3414, "GL11.GL_DEPTH_BITS");
        registerGlConstantName(3415, "GL11.GL_STENCIL_BITS");
        registerGlConstantName(3416, "GL11.GL_ACCUM_RED_BITS");
        registerGlConstantName(3417, "GL11.GL_ACCUM_GREEN_BITS");
        registerGlConstantName(3418, "GL11.GL_ACCUM_BLUE_BITS");
        registerGlConstantName(3419, "GL11.GL_ACCUM_ALPHA_BITS");
        registerGlConstantName(3440, "GL11.GL_NAME_STACK_DEPTH");
        registerGlConstantName(3456, "GL11.GL_AUTO_NORMAL");
        registerGlConstantName(3472, "GL11.GL_MAP1_COLOR_4");
        registerGlConstantName(3473, "GL11.GL_MAP1_INDEX");
        registerGlConstantName(3474, "GL11.GL_MAP1_NORMAL");
        registerGlConstantName(3475, "GL11.GL_MAP1_TEXTURE_COORD_1");
        registerGlConstantName(3476, "GL11.GL_MAP1_TEXTURE_COORD_2");
        registerGlConstantName(3477, "GL11.GL_MAP1_TEXTURE_COORD_3");
        registerGlConstantName(3478, "GL11.GL_MAP1_TEXTURE_COORD_4");
        registerGlConstantName(3479, "GL11.GL_MAP1_VERTEX_3");
        registerGlConstantName(3480, "GL11.GL_MAP1_VERTEX_4");
        registerGlConstantName(3504, "GL11.GL_MAP2_COLOR_4");
        registerGlConstantName(3505, "GL11.GL_MAP2_INDEX");
        registerGlConstantName(3506, "GL11.GL_MAP2_NORMAL");
        registerGlConstantName(3507, "GL11.GL_MAP2_TEXTURE_COORD_1");
        registerGlConstantName(3508, "GL11.GL_MAP2_TEXTURE_COORD_2");
        registerGlConstantName(3509, "GL11.GL_MAP2_TEXTURE_COORD_3");
        registerGlConstantName(3510, "GL11.GL_MAP2_TEXTURE_COORD_4");
        registerGlConstantName(3511, "GL11.GL_MAP2_VERTEX_3");
        registerGlConstantName(3512, "GL11.GL_MAP2_VERTEX_4");
        registerGlConstantName(3536, "GL11.GL_MAP1_GRID_DOMAIN");
        registerGlConstantName(3537, "GL11.GL_MAP1_GRID_SEGMENTS");
        registerGlConstantName(3538, "GL11.GL_MAP2_GRID_DOMAIN");
        registerGlConstantName(3539, "GL11.GL_MAP2_GRID_SEGMENTS");
        registerGlConstantName(3552, "GL11.GL_TEXTURE_1D");
        registerGlConstantName(3553, "GL11.GL_TEXTURE_2D");
        registerGlConstantName(3568, "GL11.GL_FEEDBACK_BUFFER_POINTER");
        registerGlConstantName(3569, "GL11.GL_FEEDBACK_BUFFER_SIZE");
        registerGlConstantName(3570, "GL11.GL_FEEDBACK_BUFFER_TYPE");
        registerGlConstantName(3571, "GL11.GL_SELECTION_BUFFER_POINTER");
        registerGlConstantName(3572, "GL11.GL_SELECTION_BUFFER_SIZE");
        registerGlConstantName(4096, "GL11.GL_TEXTURE_WIDTH");
        registerGlConstantName(4097, "GL11.GL_TEXTURE_HEIGHT");
        registerGlConstantName(4099, "GL11.GL_TEXTURE_INTERNAL_FORMAT");
        registerGlConstantName(4100, "GL11.GL_TEXTURE_BORDER_COLOR");
        registerGlConstantName(4101, "GL11.GL_TEXTURE_BORDER");
        registerGlConstantName(4352, "GL11.GL_DONT_CARE");
        registerGlConstantName(4353, "GL11.GL_FASTEST");
        registerGlConstantName(4354, "GL11.GL_NICEST");
        registerGlConstantName(16384, "GL11.GL_LIGHT0");
        registerGlConstantName(16385, "GL11.GL_LIGHT1");
        registerGlConstantName(16386, "GL11.GL_LIGHT2");
        registerGlConstantName(16387, "GL11.GL_LIGHT3");
        registerGlConstantName(16388, "GL11.GL_LIGHT4");
        registerGlConstantName(16389, "GL11.GL_LIGHT5");
        registerGlConstantName(16390, "GL11.GL_LIGHT6");
        registerGlConstantName(16391, "GL11.GL_LIGHT7");
        registerGlConstantName(4608, "GL11.GL_AMBIENT");
        registerGlConstantName(4609, "GL11.GL_DIFFUSE");
        registerGlConstantName(4610, "GL11.GL_SPECULAR");
        registerGlConstantName(4611, "GL11.GL_POSITION");
        registerGlConstantName(4612, "GL11.GL_SPOT_DIRECTION");
        registerGlConstantName(4613, "GL11.GL_SPOT_EXPONENT");
        registerGlConstantName(4614, "GL11.GL_SPOT_CUTOFF");
        registerGlConstantName(4615, "GL11.GL_CONSTANT_ATTENUATION");
        registerGlConstantName(4616, "GL11.GL_LINEAR_ATTENUATION");
        registerGlConstantName(4617, "GL11.GL_QUADRATIC_ATTENUATION");
        registerGlConstantName(4864, "GL11.GL_COMPILE");
        registerGlConstantName(4865, "GL11.GL_COMPILE_AND_EXECUTE");
        registerGlConstantName(5376, "GL11.GL_CLEAR");
        registerGlConstantName(5377, "GL11.GL_AND");
        registerGlConstantName(5378, "GL11.GL_AND_REVERSE");
        registerGlConstantName(5379, "GL11.GL_COPY");
        registerGlConstantName(5380, "GL11.GL_AND_INVERTED");
        registerGlConstantName(5381, "GL11.GL_NOOP");
        registerGlConstantName(5382, "GL11.GL_XOR");
        registerGlConstantName(5383, "GL11.GL_OR");
        registerGlConstantName(5384, "GL11.GL_NOR");
        registerGlConstantName(5385, "GL11.GL_EQUIV");
        registerGlConstantName(5386, "GL11.GL_INVERT");
        registerGlConstantName(5387, "GL11.GL_OR_REVERSE");
        registerGlConstantName(5388, "GL11.GL_COPY_INVERTED");
        registerGlConstantName(5389, "GL11.GL_OR_INVERTED");
        registerGlConstantName(5390, "GL11.GL_NAND");
        registerGlConstantName(5391, "GL11.GL_SET");
        registerGlConstantName(5632, "GL11.GL_EMISSION");
        registerGlConstantName(5633, "GL11.GL_SHININESS");
        registerGlConstantName(5634, "GL11.GL_AMBIENT_AND_DIFFUSE");
        registerGlConstantName(5635, "GL11.GL_COLOR_INDEXES");
        registerGlConstantName(5888, "GL11.GL_MODELVIEW");
        registerGlConstantName(5889, "GL11.GL_PROJECTION");
        registerGlConstantName(5890, "GL11.GL_TEXTURE");
        registerGlConstantName(6144, "GL11.GL_COLOR");
        registerGlConstantName(6145, "GL11.GL_DEPTH");
        registerGlConstantName(6146, "GL11.GL_STENCIL");
        registerGlConstantName(6400, "GL11.GL_COLOR_INDEX");
        registerGlConstantName(6401, "GL11.GL_STENCIL_INDEX");
        registerGlConstantName(6402, "GL11.GL_DEPTH_COMPONENT");
        registerGlConstantName(6403, "GL11.GL_RED");
        registerGlConstantName(6404, "GL11.GL_GREEN");
        registerGlConstantName(6405, "GL11.GL_BLUE");
        registerGlConstantName(6406, "GL11.GL_ALPHA");
        registerGlConstantName(6407, "GL11.GL_RGB");
        registerGlConstantName(6408, "GL11.GL_RGBA");
        registerGlConstantName(6409, "GL11.GL_LUMINANCE");
        registerGlConstantName(6410, "GL11.GL_LUMINANCE_ALPHA");
        registerGlConstantName(6656, "GL11.GL_BITMAP");
        registerGlConstantName(6912, "GL11.GL_POINT");
        registerGlConstantName(6913, "GL11.GL_LINE");
        registerGlConstantName(6914, "GL11.GL_FILL");
        registerGlConstantName(7168, "GL11.GL_RENDER");
        registerGlConstantName(7169, "GL11.GL_FEEDBACK");
        registerGlConstantName(7170, "GL11.GL_SELECT");
        registerGlConstantName(7424, "GL11.GL_FLAT");
        registerGlConstantName(7425, "GL11.GL_SMOOTH");
        registerGlConstantName(7680, "GL11.GL_KEEP");
        registerGlConstantName(7681, "GL11.GL_REPLACE");
        registerGlConstantName(7682, "GL11.GL_INCR");
        registerGlConstantName(7683, "GL11.GL_DECR");
        registerGlConstantName(7936, "GL11.GL_VENDOR");
        registerGlConstantName(7937, "GL11.GL_RENDERER");
        registerGlConstantName(7938, "GL11.GL_VERSION");
        registerGlConstantName(7939, "GL11.GL_EXTENSIONS");
        registerGlConstantName(8192, "GL11.GL_S");
        registerGlConstantName(8193, "GL11.GL_T");
        registerGlConstantName(8194, "GL11.GL_R");
        registerGlConstantName(8195, "GL11.GL_Q");
        registerGlConstantName(8448, "GL11.GL_MODULATE");
        registerGlConstantName(8449, "GL11.GL_DECAL");
        registerGlConstantName(8704, "GL11.GL_TEXTURE_ENV_MODE");
        registerGlConstantName(8705, "GL11.GL_TEXTURE_ENV_COLOR");
        registerGlConstantName(8960, "GL11.GL_TEXTURE_ENV");
        registerGlConstantName(9216, "GL11.GL_EYE_LINEAR");
        registerGlConstantName(9217, "GL11.GL_OBJECT_LINEAR");
        registerGlConstantName(9218, "GL11.GL_SPHERE_MAP");
        registerGlConstantName(9472, "GL11.GL_TEXTURE_GEN_MODE");
        registerGlConstantName(9473, "GL11.GL_OBJECT_PLANE");
        registerGlConstantName(9474, "GL11.GL_EYE_PLANE");
        registerGlConstantName(9728, "GL11.GL_NEAREST");
        registerGlConstantName(9729, "GL11.GL_LINEAR");
        registerGlConstantName(9984, "GL11.GL_NEAREST_MIPMAP_NEAREST");
        registerGlConstantName(9985, "GL11.GL_LINEAR_MIPMAP_NEAREST");
        registerGlConstantName(9986, "GL11.GL_NEAREST_MIPMAP_LINEAR");
        registerGlConstantName(9987, "GL11.GL_LINEAR_MIPMAP_LINEAR");
        registerGlConstantName(10240, "GL11.GL_TEXTURE_MAG_FILTER");
        registerGlConstantName(10241, "GL11.GL_TEXTURE_MIN_FILTER");
        registerGlConstantName(10242, "GL11.GL_TEXTURE_WRAP_S");
        registerGlConstantName(10243, "GL11.GL_TEXTURE_WRAP_T");
        registerGlConstantName(10496, "GL11.GL_CLAMP");
        registerGlConstantName(10497, "GL11.GL_REPEAT");
        registerGlConstantName(-1, "GL11.GL_ALL_CLIENT_ATTRIB_BITS");
        registerGlConstantName(32824, "GL11.GL_POLYGON_OFFSET_FACTOR");
        registerGlConstantName(10752, "GL11.GL_POLYGON_OFFSET_UNITS");
        registerGlConstantName(10753, "GL11.GL_POLYGON_OFFSET_POINT");
        registerGlConstantName(10754, "GL11.GL_POLYGON_OFFSET_LINE");
        registerGlConstantName(32823, "GL11.GL_POLYGON_OFFSET_FILL");
        registerGlConstantName(32827, "GL11.GL_ALPHA4");
        registerGlConstantName(32828, "GL11.GL_ALPHA8");
        registerGlConstantName(32829, "GL11.GL_ALPHA12");
        registerGlConstantName(32830, "GL11.GL_ALPHA16");
        registerGlConstantName(32831, "GL11.GL_LUMINANCE4");
        registerGlConstantName(32832, "GL11.GL_LUMINANCE8");
        registerGlConstantName(32833, "GL11.GL_LUMINANCE12");
        registerGlConstantName(32834, "GL11.GL_LUMINANCE16");
        registerGlConstantName(32835, "GL11.GL_LUMINANCE4_ALPHA4");
        registerGlConstantName(32836, "GL11.GL_LUMINANCE6_ALPHA2");
        registerGlConstantName(32837, "GL11.GL_LUMINANCE8_ALPHA8");
        registerGlConstantName(32838, "GL11.GL_LUMINANCE12_ALPHA4");
        registerGlConstantName(32839, "GL11.GL_LUMINANCE12_ALPHA12");
        registerGlConstantName(32840, "GL11.GL_LUMINANCE16_ALPHA16");
        registerGlConstantName(32841, "GL11.GL_INTENSITY");
        registerGlConstantName(32842, "GL11.GL_INTENSITY4");
        registerGlConstantName(32843, "GL11.GL_INTENSITY8");
        registerGlConstantName(32844, "GL11.GL_INTENSITY12");
        registerGlConstantName(32845, "GL11.GL_INTENSITY16");
        registerGlConstantName(10768, "GL11.GL_R3_G3_B2");
        registerGlConstantName(32847, "GL11.GL_RGB4");
        registerGlConstantName(32848, "GL11.GL_RGB5");
        registerGlConstantName(32849, "GL11.GL_RGB8");
        registerGlConstantName(32850, "GL11.GL_RGB10");
        registerGlConstantName(32851, "GL11.GL_RGB12");
        registerGlConstantName(32852, "GL11.GL_RGB16");
        registerGlConstantName(32853, "GL11.GL_RGBA2");
        registerGlConstantName(32854, "GL11.GL_RGBA4");
        registerGlConstantName(32855, "GL11.GL_RGB5_A1");
        registerGlConstantName(32856, "GL11.GL_RGBA8");
        registerGlConstantName(32857, "GL11.GL_RGB10_A2");
        registerGlConstantName(32858, "GL11.GL_RGBA12");
        registerGlConstantName(32859, "GL11.GL_RGBA16");
        registerGlConstantName(32860, "GL11.GL_TEXTURE_RED_SIZE");
        registerGlConstantName(32861, "GL11.GL_TEXTURE_GREEN_SIZE");
        registerGlConstantName(32862, "GL11.GL_TEXTURE_BLUE_SIZE");
        registerGlConstantName(32863, "GL11.GL_TEXTURE_ALPHA_SIZE");
        registerGlConstantName(32864, "GL11.GL_TEXTURE_LUMINANCE_SIZE");
        registerGlConstantName(32865, "GL11.GL_TEXTURE_INTENSITY_SIZE");
        registerGlConstantName(32867, "GL11.GL_PROXY_TEXTURE_1D");
        registerGlConstantName(32868, "GL11.GL_PROXY_TEXTURE_2D");
        registerGlConstantName(32870, "GL11.GL_TEXTURE_PRIORITY");
        registerGlConstantName(32871, "GL11.GL_TEXTURE_RESIDENT");
        registerGlConstantName(32872, "GL11.GL_TEXTURE_BINDING_1D");
        registerGlConstantName(32873, "GL11.GL_TEXTURE_BINDING_2D");
        registerGlConstantName(32884, "GL11.GL_VERTEX_ARRAY");
        registerGlConstantName(32885, "GL11.GL_NORMAL_ARRAY");
        registerGlConstantName(32886, "GL11.GL_COLOR_ARRAY");
        registerGlConstantName(32887, "GL11.GL_INDEX_ARRAY");
        registerGlConstantName(32888, "GL11.GL_TEXTURE_COORD_ARRAY");
        registerGlConstantName(32889, "GL11.GL_EDGE_FLAG_ARRAY");
        registerGlConstantName(32890, "GL11.GL_VERTEX_ARRAY_SIZE");
        registerGlConstantName(32891, "GL11.GL_VERTEX_ARRAY_TYPE");
        registerGlConstantName(32892, "GL11.GL_VERTEX_ARRAY_STRIDE");
        registerGlConstantName(32894, "GL11.GL_NORMAL_ARRAY_TYPE");
        registerGlConstantName(32895, "GL11.GL_NORMAL_ARRAY_STRIDE");
        registerGlConstantName(32897, "GL11.GL_COLOR_ARRAY_SIZE");
        registerGlConstantName(32898, "GL11.GL_COLOR_ARRAY_TYPE");
        registerGlConstantName(32899, "GL11.GL_COLOR_ARRAY_STRIDE");
        registerGlConstantName(32901, "GL11.GL_INDEX_ARRAY_TYPE");
        registerGlConstantName(32902, "GL11.GL_INDEX_ARRAY_STRIDE");
        registerGlConstantName(32904, "GL11.GL_TEXTURE_COORD_ARRAY_SIZE");
        registerGlConstantName(32905, "GL11.GL_TEXTURE_COORD_ARRAY_TYPE");
        registerGlConstantName(32906, "GL11.GL_TEXTURE_COORD_ARRAY_STRIDE");
        registerGlConstantName(32908, "GL11.GL_EDGE_FLAG_ARRAY_STRIDE");
        registerGlConstantName(32910, "GL11.GL_VERTEX_ARRAY_POINTER");
        registerGlConstantName(32911, "GL11.GL_NORMAL_ARRAY_POINTER");
        registerGlConstantName(32912, "GL11.GL_COLOR_ARRAY_POINTER");
        registerGlConstantName(32913, "GL11.GL_INDEX_ARRAY_POINTER");
        registerGlConstantName(32914, "GL11.GL_TEXTURE_COORD_ARRAY_POINTER");
        registerGlConstantName(32915, "GL11.GL_EDGE_FLAG_ARRAY_POINTER");
        registerGlConstantName(10784, "GL11.GL_V2F");
        registerGlConstantName(10785, "GL11.GL_V3F");
        registerGlConstantName(10786, "GL11.GL_C4UB_V2F");
        registerGlConstantName(10787, "GL11.GL_C4UB_V3F");
        registerGlConstantName(10788, "GL11.GL_C3F_V3F");
        registerGlConstantName(10789, "GL11.GL_N3F_V3F");
        registerGlConstantName(10790, "GL11.GL_C4F_N3F_V3F");
        registerGlConstantName(10791, "GL11.GL_T2F_V3F");
        registerGlConstantName(10792, "GL11.GL_T4F_V4F");
        registerGlConstantName(10793, "GL11.GL_T2F_C4UB_V3F");
        registerGlConstantName(10794, "GL11.GL_T2F_C3F_V3F");
        registerGlConstantName(10795, "GL11.GL_T2F_N3F_V3F");
        registerGlConstantName(10796, "GL11.GL_T2F_C4F_N3F_V3F");
        registerGlConstantName(10797, "GL11.GL_T4F_C4F_N3F_V4F");
        registerGlConstantName(3057, "GL11.GL_LOGIC_OP");
        registerGlConstantName(4099, "GL11.GL_TEXTURE_COMPONENTS");
        registerGlConstantName(32874, "GL12.GL_TEXTURE_BINDING_3D");
        registerGlConstantName(32875, "GL12.GL_PACK_SKIP_IMAGES");
        registerGlConstantName(32876, "GL12.GL_PACK_IMAGE_HEIGHT");
        registerGlConstantName(32877, "GL12.GL_UNPACK_SKIP_IMAGES");
        registerGlConstantName(32878, "GL12.GL_UNPACK_IMAGE_HEIGHT");
        registerGlConstantName(32879, "GL12.GL_TEXTURE_3D");
        registerGlConstantName(32880, "GL12.GL_PROXY_TEXTURE_3D");
        registerGlConstantName(32881, "GL12.GL_TEXTURE_DEPTH");
        registerGlConstantName(32882, "GL12.GL_TEXTURE_WRAP_R");
        registerGlConstantName(32883, "GL12.GL_MAX_3D_TEXTURE_SIZE");
        registerGlConstantName(32992, "GL12.GL_BGR");
        registerGlConstantName(32993, "GL12.GL_BGRA");
        registerGlConstantName(32818, "GL12.GL_UNSIGNED_BYTE_3_3_2");
        registerGlConstantName(33634, "GL12.GL_UNSIGNED_BYTE_2_3_3_REV");
        registerGlConstantName(33635, "GL12.GL_UNSIGNED_SHORT_5_6_5");
        registerGlConstantName(33636, "GL12.GL_UNSIGNED_SHORT_5_6_5_REV");
        registerGlConstantName(32819, "GL12.GL_UNSIGNED_SHORT_4_4_4_4");
        registerGlConstantName(33637, "GL12.GL_UNSIGNED_SHORT_4_4_4_4_REV");
        registerGlConstantName(32820, "GL12.GL_UNSIGNED_SHORT_5_5_5_1");
        registerGlConstantName(33638, "GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV");
        registerGlConstantName(32821, "GL12.GL_UNSIGNED_INT_8_8_8_8");
        registerGlConstantName(33639, "GL12.GL_UNSIGNED_INT_8_8_8_8_REV");
        registerGlConstantName(32822, "GL12.GL_UNSIGNED_INT_10_10_10_2");
        registerGlConstantName(33640, "GL12.GL_UNSIGNED_INT_2_10_10_10_REV");
        registerGlConstantName(32826, "GL12.GL_RESCALE_NORMAL");
        registerGlConstantName(33272, "GL12.GL_LIGHT_MODEL_COLOR_CONTROL");
        registerGlConstantName(33273, "GL12.GL_SINGLE_COLOR");
        registerGlConstantName(33274, "GL12.GL_SEPARATE_SPECULAR_COLOR");
        registerGlConstantName(33071, "GL12.GL_CLAMP_TO_EDGE");
        registerGlConstantName(33082, "GL12.GL_TEXTURE_MIN_LOD");
        registerGlConstantName(33083, "GL12.GL_TEXTURE_MAX_LOD");
        registerGlConstantName(33084, "GL12.GL_TEXTURE_BASE_LEVEL");
        registerGlConstantName(33085, "GL12.GL_TEXTURE_MAX_LEVEL");
        registerGlConstantName(33000, "GL12.GL_MAX_ELEMENTS_VERTICES");
        registerGlConstantName(33001, "GL12.GL_MAX_ELEMENTS_INDICES");
        registerGlConstantName(33901, "GL12.GL_ALIASED_POINT_SIZE_RANGE");
        registerGlConstantName(33902, "GL12.GL_ALIASED_LINE_WIDTH_RANGE");
        registerGlConstantName(33984, "GL13.GL_TEXTURE0");
        registerGlConstantName(33985, "GL13.GL_TEXTURE1");
        registerGlConstantName(33986, "GL13.GL_TEXTURE2");
        registerGlConstantName(33987, "GL13.GL_TEXTURE3");
        registerGlConstantName(33988, "GL13.GL_TEXTURE4");
        registerGlConstantName(33989, "GL13.GL_TEXTURE5");
        registerGlConstantName(33990, "GL13.GL_TEXTURE6");
        registerGlConstantName(33991, "GL13.GL_TEXTURE7");
        registerGlConstantName(33992, "GL13.GL_TEXTURE8");
        registerGlConstantName(33993, "GL13.GL_TEXTURE9");
        registerGlConstantName(33994, "GL13.GL_TEXTURE10");
        registerGlConstantName(33995, "GL13.GL_TEXTURE11");
        registerGlConstantName(33996, "GL13.GL_TEXTURE12");
        registerGlConstantName(33997, "GL13.GL_TEXTURE13");
        registerGlConstantName(33998, "GL13.GL_TEXTURE14");
        registerGlConstantName(33999, "GL13.GL_TEXTURE15");
        registerGlConstantName(34000, "GL13.GL_TEXTURE16");
        registerGlConstantName(34001, "GL13.GL_TEXTURE17");
        registerGlConstantName(34002, "GL13.GL_TEXTURE18");
        registerGlConstantName(34003, "GL13.GL_TEXTURE19");
        registerGlConstantName(34004, "GL13.GL_TEXTURE20");
        registerGlConstantName(34005, "GL13.GL_TEXTURE21");
        registerGlConstantName(34006, "GL13.GL_TEXTURE22");
        registerGlConstantName(34007, "GL13.GL_TEXTURE23");
        registerGlConstantName(34008, "GL13.GL_TEXTURE24");
        registerGlConstantName(34009, "GL13.GL_TEXTURE25");
        registerGlConstantName(34010, "GL13.GL_TEXTURE26");
        registerGlConstantName(34011, "GL13.GL_TEXTURE27");
        registerGlConstantName(34012, "GL13.GL_TEXTURE28");
        registerGlConstantName(34013, "GL13.GL_TEXTURE29");
        registerGlConstantName(34014, "GL13.GL_TEXTURE30");
        registerGlConstantName(34015, "GL13.GL_TEXTURE31");
        registerGlConstantName(34016, "GL13.GL_ACTIVE_TEXTURE");
        registerGlConstantName(34017, "GL13.GL_CLIENT_ACTIVE_TEXTURE");
        registerGlConstantName(34018, "GL13.GL_MAX_TEXTURE_UNITS");
        registerGlConstantName(34065, "GL13.GL_NORMAL_MAP");
        registerGlConstantName(34066, "GL13.GL_REFLECTION_MAP");
        registerGlConstantName(34067, "GL13.GL_TEXTURE_CUBE_MAP");
        registerGlConstantName(34068, "GL13.GL_TEXTURE_BINDING_CUBE_MAP");
        registerGlConstantName(34069, "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X");
        registerGlConstantName(34070, "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X");
        registerGlConstantName(34071, "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y");
        registerGlConstantName(34072, "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y");
        registerGlConstantName(34073, "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z");
        registerGlConstantName(34074, "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z");
        registerGlConstantName(34075, "GL13.GL_PROXY_TEXTURE_CUBE_MAP");
        registerGlConstantName(34076, "GL13.GL_MAX_CUBE_MAP_TEXTURE_SIZE");
        registerGlConstantName(34025, "GL13.GL_COMPRESSED_ALPHA");
        registerGlConstantName(34026, "GL13.GL_COMPRESSED_LUMINANCE");
        registerGlConstantName(34027, "GL13.GL_COMPRESSED_LUMINANCE_ALPHA");
        registerGlConstantName(34028, "GL13.GL_COMPRESSED_INTENSITY");
        registerGlConstantName(34029, "GL13.GL_COMPRESSED_RGB");
        registerGlConstantName(34030, "GL13.GL_COMPRESSED_RGBA");
        registerGlConstantName(34031, "GL13.GL_TEXTURE_COMPRESSION_HINT");
        registerGlConstantName(34464, "GL13.GL_TEXTURE_COMPRESSED_IMAGE_SIZE");
        registerGlConstantName(34465, "GL13.GL_TEXTURE_COMPRESSED");
        registerGlConstantName(34466, "GL13.GL_NUM_COMPRESSED_TEXTURE_FORMATS");
        registerGlConstantName(34467, "GL13.GL_COMPRESSED_TEXTURE_FORMATS");
        registerGlConstantName(32925, "GL13.GL_MULTISAMPLE");
        registerGlConstantName(32926, "GL13.GL_SAMPLE_ALPHA_TO_COVERAGE");
        registerGlConstantName(32927, "GL13.GL_SAMPLE_ALPHA_TO_ONE");
        registerGlConstantName(32928, "GL13.GL_SAMPLE_COVERAGE");
        registerGlConstantName(32936, "GL13.GL_SAMPLE_BUFFERS");
        registerGlConstantName(32937, "GL13.GL_SAMPLES");
        registerGlConstantName(32938, "GL13.GL_SAMPLE_COVERAGE_VALUE");
        registerGlConstantName(32939, "GL13.GL_SAMPLE_COVERAGE_INVERT");
        registerGlConstantName(34019, "GL13.GL_TRANSPOSE_MODELVIEW_MATRIX");
        registerGlConstantName(34020, "GL13.GL_TRANSPOSE_PROJECTION_MATRIX");
        registerGlConstantName(34021, "GL13.GL_TRANSPOSE_TEXTURE_MATRIX");
        registerGlConstantName(34022, "GL13.GL_TRANSPOSE_COLOR_MATRIX");
        registerGlConstantName(34160, "GL13.GL_COMBINE");
        registerGlConstantName(34161, "GL13.GL_COMBINE_RGB");
        registerGlConstantName(34162, "GL13.GL_COMBINE_ALPHA");
        registerGlConstantName(34176, "GL13.GL_SOURCE0_RGB");
        registerGlConstantName(34177, "GL13.GL_SOURCE1_RGB");
        registerGlConstantName(34178, "GL13.GL_SOURCE2_RGB");
        registerGlConstantName(34184, "GL13.GL_SOURCE0_ALPHA");
        registerGlConstantName(34185, "GL13.GL_SOURCE1_ALPHA");
        registerGlConstantName(34186, "GL13.GL_SOURCE2_ALPHA");
        registerGlConstantName(34192, "GL13.GL_OPERAND0_RGB");
        registerGlConstantName(34193, "GL13.GL_OPERAND1_RGB");
        registerGlConstantName(34194, "GL13.GL_OPERAND2_RGB");
        registerGlConstantName(34200, "GL13.GL_OPERAND0_ALPHA");
        registerGlConstantName(34201, "GL13.GL_OPERAND1_ALPHA");
        registerGlConstantName(34202, "GL13.GL_OPERAND2_ALPHA");
        registerGlConstantName(34163, "GL13.GL_RGB_SCALE");
        registerGlConstantName(34164, "GL13.GL_ADD_SIGNED");
        registerGlConstantName(34165, "GL13.GL_INTERPOLATE");
        registerGlConstantName(34023, "GL13.GL_SUBTRACT");
        registerGlConstantName(34166, "GL13.GL_CONSTANT");
        registerGlConstantName(34167, "GL13.GL_PRIMARY_COLOR");
        registerGlConstantName(34168, "GL13.GL_PREVIOUS");
        registerGlConstantName(34478, "GL13.GL_DOT3_RGB");
        registerGlConstantName(34479, "GL13.GL_DOT3_RGBA");
        registerGlConstantName(33069, "GL13.GL_CLAMP_TO_BORDER");
        registerGlConstantName(33169, "GL14.GL_GENERATE_MIPMAP");
        registerGlConstantName(33170, "GL14.GL_GENERATE_MIPMAP_HINT");
        registerGlConstantName(33189, "GL14.GL_DEPTH_COMPONENT16");
        registerGlConstantName(33190, "GL14.GL_DEPTH_COMPONENT24");
        registerGlConstantName(33191, "GL14.GL_DEPTH_COMPONENT32");
        registerGlConstantName(34890, "GL14.GL_TEXTURE_DEPTH_SIZE");
        registerGlConstantName(34891, "GL14.GL_DEPTH_TEXTURE_MODE");
        registerGlConstantName(34892, "GL14.GL_TEXTURE_COMPARE_MODE");
        registerGlConstantName(34893, "GL14.GL_TEXTURE_COMPARE_FUNC");
        registerGlConstantName(34894, "GL14.GL_COMPARE_R_TO_TEXTURE");
        registerGlConstantName(33872, "GL14.GL_FOG_COORDINATE_SOURCE");
        registerGlConstantName(33873, "GL14.GL_FOG_COORDINATE");
        registerGlConstantName(33874, "GL14.GL_FRAGMENT_DEPTH");
        registerGlConstantName(33875, "GL14.GL_CURRENT_FOG_COORDINATE");
        registerGlConstantName(33876, "GL14.GL_FOG_COORDINATE_ARRAY_TYPE");
        registerGlConstantName(33877, "GL14.GL_FOG_COORDINATE_ARRAY_STRIDE");
        registerGlConstantName(33878, "GL14.GL_FOG_COORDINATE_ARRAY_POINTER");
        registerGlConstantName(33879, "GL14.GL_FOG_COORDINATE_ARRAY");
        registerGlConstantName(33062, "GL14.GL_POINT_SIZE_MIN");
        registerGlConstantName(33063, "GL14.GL_POINT_SIZE_MAX");
        registerGlConstantName(33064, "GL14.GL_POINT_FADE_THRESHOLD_SIZE");
        registerGlConstantName(33065, "GL14.GL_POINT_DISTANCE_ATTENUATION");
        registerGlConstantName(33880, "GL14.GL_COLOR_SUM");
        registerGlConstantName(33881, "GL14.GL_CURRENT_SECONDARY_COLOR");
        registerGlConstantName(33882, "GL14.GL_SECONDARY_COLOR_ARRAY_SIZE");
        registerGlConstantName(33883, "GL14.GL_SECONDARY_COLOR_ARRAY_TYPE");
        registerGlConstantName(33884, "GL14.GL_SECONDARY_COLOR_ARRAY_STRIDE");
        registerGlConstantName(33885, "GL14.GL_SECONDARY_COLOR_ARRAY_POINTER");
        registerGlConstantName(33886, "GL14.GL_SECONDARY_COLOR_ARRAY");
        registerGlConstantName(32968, "GL14.GL_BLEND_DST_RGB");
        registerGlConstantName(32969, "GL14.GL_BLEND_SRC_RGB");
        registerGlConstantName(32970, "GL14.GL_BLEND_DST_ALPHA");
        registerGlConstantName(32971, "GL14.GL_BLEND_SRC_ALPHA");
        registerGlConstantName(34055, "GL14.GL_INCR_WRAP");
        registerGlConstantName(34056, "GL14.GL_DECR_WRAP");
        registerGlConstantName(34048, "GL14.GL_TEXTURE_FILTER_CONTROL");
        registerGlConstantName(34049, "GL14.GL_TEXTURE_LOD_BIAS");
        registerGlConstantName(34045, "GL14.GL_MAX_TEXTURE_LOD_BIAS");
        registerGlConstantName(33648, "GL14.GL_MIRRORED_REPEAT");
        registerGlConstantName(32773, "ARBImaging.GL_BLEND_COLOR");
        registerGlConstantName(32777, "ARBImaging.GL_BLEND_EQUATION");
        registerGlConstantName(32774, "GL14.GL_FUNC_ADD");
        registerGlConstantName(32778, "GL14.GL_FUNC_SUBTRACT");
        registerGlConstantName(32779, "GL14.GL_FUNC_REVERSE_SUBTRACT");
        registerGlConstantName(32775, "GL14.GL_MIN");
        registerGlConstantName(32776, "GL14.GL_MAX");
        registerGlConstantName(34962, "GL15.GL_ARRAY_BUFFER");
        registerGlConstantName(34963, "GL15.GL_ELEMENT_ARRAY_BUFFER");
        registerGlConstantName(34964, "GL15.GL_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34965, "GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34966, "GL15.GL_VERTEX_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34967, "GL15.GL_NORMAL_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34968, "GL15.GL_COLOR_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34969, "GL15.GL_INDEX_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34970, "GL15.GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34971, "GL15.GL_EDGE_FLAG_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34972, "GL15.GL_SECONDARY_COLOR_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34973, "GL15.GL_FOG_COORDINATE_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34974, "GL15.GL_WEIGHT_ARRAY_BUFFER_BINDING");
        registerGlConstantName(34975, "GL15.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING");
        registerGlConstantName(35040, "GL15.GL_STREAM_DRAW");
        registerGlConstantName(35041, "GL15.GL_STREAM_READ");
        registerGlConstantName(35042, "GL15.GL_STREAM_COPY");
        registerGlConstantName(35044, "GL15.GL_STATIC_DRAW");
        registerGlConstantName(35045, "GL15.GL_STATIC_READ");
        registerGlConstantName(35046, "GL15.GL_STATIC_COPY");
        registerGlConstantName(35048, "GL15.GL_DYNAMIC_DRAW");
        registerGlConstantName(35049, "GL15.GL_DYNAMIC_READ");
        registerGlConstantName(35050, "GL15.GL_DYNAMIC_COPY");
        registerGlConstantName(35000, "GL15.GL_READ_ONLY");
        registerGlConstantName(35001, "GL15.GL_WRITE_ONLY");
        registerGlConstantName(35002, "GL15.GL_READ_WRITE");
        registerGlConstantName(34660, "GL15.GL_BUFFER_SIZE");
        registerGlConstantName(34661, "GL15.GL_BUFFER_USAGE");
        registerGlConstantName(35003, "GL15.GL_BUFFER_ACCESS");
        registerGlConstantName(35004, "GL15.GL_BUFFER_MAPPED");
        registerGlConstantName(35005, "GL15.GL_BUFFER_MAP_POINTER");
        registerGlConstantName(34138, "NVFogDistance.GL_FOG_DISTANCE_MODE_NV");
        registerGlConstantName(34139, "NVFogDistance.GL_EYE_RADIAL_NV");
        registerGlConstantName(34140, "NVFogDistance.GL_EYE_PLANE_ABSOLUTE_NV");
    }
}
