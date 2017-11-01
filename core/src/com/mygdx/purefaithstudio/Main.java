package com.mygdx.purefaithstudio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;

public class Main extends Base {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Vector2 touch;
    private Texture texture[];
    //private ParticleLayer partlay;
    //private FrameBuffer fbo;
    //private TextureRegion fbr;
    private float accelX=0,lastAccelX=0,thresh=0.3f,fact=0.4f;
    private float accelY=0,lastAccelY=0;
    //private float accelZ=0,lastAccelZ=0;
    private float dipMul=0.0f,touchcount=0,moveX,moveY;
    private int size=0;
    private boolean /*parallax = false,*/gyroscope=false;
    private BitmapFont font;

    public Main(Game game, com.mygdx.purefaithstudio.Resolver resolver) {
        super(game, resolver);
        // Never put "show" part here
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);
    }

    @Override
    public void show() {
        Config.load();
        Config.loadwcd();
        resetCamera(480,800);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        if(/*Config.useGyro &&*/ !gyroscope) {
            gyroscope = Gdx.input.isPeripheralAvailable(Peripheral.Gyroscope);
        }
        setInputProcessor();
        /*if(!parallax) {
            setInputProcessor();
        }*/
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        loadImageTexture();
        /*partlay = new ParticleLayer();
        partlay.loadeffect();
        if (fbo == null) {
            fbo = new FrameBuffer(Format.RGBA8888, 480, 800, false);
            fbr = new TextureRegion(fbo.getColorBufferTexture());
            fbr.flip(false, true);
        }*/
        accelY = 0;
        accelX = 0;
        //batch.setShader(partlay.shaderProgram);
        // resetCamera(sW,sH);
    }
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        /*if(partlay !=null)
            partlay.dispose();
        if(fbo != null)
            fbo.dispose();
        fbr.getTexture().dispose();
        fbo = null;
        fbr=null;*/
        if(texture !=null)
            for(Texture t:texture) {
                if (t != null)
                    t.dispose();
            }
        texture = null;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        dispose();
        show();
    }

    @Override
    public void render(float delta) {
        /*if (!(Config.listTest.equals(ParticleLayer.EveNo))) {
            Gdx.app.log("harsim","partlay reloaded");
           /* if(partlay!=null)
                partlay.loadeffect();
            loadImageTexture();
        }*/
        if(/*Config.useGyro &&*/ !gyroscope) {
            gyroscope = Gdx.input.isPeripheralAvailable(Peripheral.Gyroscope);
        }
        //Gdx.app.log("harsim","render");
        delta = delta > 0.2f ? 0.2f : delta;
        /*if(partlay!=null)
            partlay.update(delta);*/
        //accelerometer
        if(gyroscope)
            accelX-=Gdx.input.getGyroscopeY() * 0.7f;//roll
        else
            accelX = Gdx.input.getAccelerometerX();
        if(accelX > 7) accelX = 7;
        if(accelX < -7) accelX = -7;
        accelX = accelX * fact+ lastAccelX * (1-fact);

        if(gyroscope)
            accelY+=Gdx.input.getGyroscopeX() * 0.7f;//pitch
        else
            accelY = Gdx.input.getAccelerometerY() -4.5f;

        if(accelY > 7) accelY = 7;
        if(accelY < -7) accelY = -7;
        accelY = accelY * fact+ lastAccelY * (1-fact) ;
	    /*accelZ = Gdx.input.getAccelerometerZ() ;
        accelZ = accelZ * fact+ lastAccelZ * (1-fact);*/

        draw(delta); // Main draw part

        if (Math.abs(accelX - lastAccelX) > thresh) {
            lastAccelX = accelX;
        }
        if (Math.abs(accelY - lastAccelY) > thresh) {
            lastAccelY = accelY;
        }

        /*if (Math.abs(accelZ - lastAccelZ) > thresh) {
        lastAccelZ = accelZ;
        }*/

        if(gyroscope && touchcount > 1){
            accelY = 0;
            accelX = 0;
        }
        if (isAndroid)
            limitFPS();

    }

    private void draw(float delta) {
        super.render(delta);
        //Gdx.gl.glClearColor(0, 0, 0, 1);
        //Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glClearColor(Config.backColor[0] / 255.0f, Config.backColor[1] / 255.0f, Config.backColor[2] / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (isAndroid && resolver != null) // In daydream resolver is null
            camera.position.x = (480 / 2) - resolver.getxPixelOffset();

        camera.update();
        batch.begin();
        if(texture !=null) {
            for (int i = size - 1; i > 0; i--) {
                if (texture[i] != null) {
                    dipMul = i*Config.Sensitivity;
                    moveX = accelX * dipMul;
                    moveY = accelY * dipMul;
                    /*if (moveX > 7 * dipMul) moveX = 7 * dipMul;
                    if (moveX < -(7 * dipMul)) moveX = -7 * dipMul;
                    if (moveY > 7 * dipMul) moveY = 7 * dipMul;
                    if (moveY < -(7 * dipMul)) moveY = -7 * dipMul;*/
                    //if (moveY > 10 * dipMul || moveY < -(10 * dipMul)) accelY =0;
                    batch.draw(texture[i], -(7 * dipMul) + moveX, -(7 * dipMul)+ moveY, 480+(14*dipMul), 800+(14*dipMul));
                }
            }
        }

        if(size>0 && texture[0] != null) {

            /*if(parallax) {
                batch.draw(texture[0], 0, 0, 480, 800);
            }
            else {*/
                batch.draw(texture[0], -20+ accelX * 2, 0, 520, 800);
            /*}*/
        }

        //batch.setShader(null);
       /* if (partlay.pe !=null) {
            fbo.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            //batch.setColor(Config.color[0]/255.0f, Config.color[1] / 255.0f, Config.color[2]/255.0f,0.6f);
            //batch.setColor(1,1,1,0.8f);
            partlay.pe.setEmittersCleanUpBlendFunction(false);
            partlay.render(batch);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            //batch.setColor(1,1,1,1);

            fbo.end();
            //particle effects layer
            batch.draw(fbr, 0, 0, 480, 800);
        }*/
        //onscreen acclero debug
       /*font.draw(batch,"roll:"+accelX,10,780);
        font.draw(batch,"pitch:"+accelY,10,760);*/
       /* font.draw(batch,"rotation"+accelZ,10,740);*/
        batch.end();
    }

    private void resetCamera(float sW, float sH) {
        camera.setToOrtho(false, sW, sH);
        // camera.position.set(sW / 2, sH / 2, 0);
    }
    public void setInputProcessor(){
        Gdx.input.setInputProcessor(new InputProcessor() {

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                // TODO Auto-generated method stub
               touchcount--;
                /* if (com.mygdx.purefaithstudio.Config.persistent) {
                    //partlay.setWind((240 - touch.x) * 0.2f);
                    if(partlay!=null)
                        partlay.setScale(0.6f);
                }*/
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                // TODO Auto-generated method stub
               /* touch.x = (int) (screenX * 480 / sW);
                touch.y = (int) (screenY * 800 / sH);
                touch.y = 800 - touch.y;
                if (Config.persistent)
                    partlay.setWind((240 - touch.x) * 0.2f);*/
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // TODO Auto-generated method stub

                touchcount++;
                 /*   /*touch = new Vector2();
                    touch.x = (int) (screenX * 480 / Gdx.graphics.getWidth());
                    touch.y = (int) (screenY * 800 / Gdx.graphics.getHeight());
                    touch.y = 800 - touch.y;
                    System.out.println(touch.x+":"+touch.y);*/
                /*if (com.mygdx.purefaithstudio.Config.persistent) {
                    //partlay.setWind((240 - touch.x) * 0.2f);
                    if(partlay!=null) {
                        partlay.setScale(1 / 0.6f);
                    }
                }*/
				/*fire.setPos(touch.x, touch.y);
				if (Config.moving) {
				}
				moved = true;
				timer = 4;*/
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                // TODO Auto-generated method stub
                if (!isAndroid && Gdx.input.isKeyPressed(Keys.ESCAPE))
                    Gdx.app.exit();
                if(!isAndroid && Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    Config.listTest = ""+(Integer.parseInt(Config.listTest) + 1);
                }
                if(!isAndroid && Gdx.input.isKeyPressed(Keys.LEFT)) {
                    Config.listTest = ""+(Integer.parseInt(Config.listTest) - 1);
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                // TODO Auto-generated method stub

                return false;
            }
        });
    }

    public void loadImageTexture(){
        if(texture !=null) {
            for (Texture t : texture) {
                if (t != null)
                    t.dispose();
            }
            texture = null;
        }
        size = Config.wcd.getSize();
        //parallax = wcd.isParallax();
        texture = new Texture[size];
        String[] layers = Config.wcd.getLayers();
        for(int i = 0; i < size;i ++) {
            System.out.println(Gdx.files.local("LWPData/"+Config.wcd.getDirectorName()+"/"+layers[i]).path());
            texture[i] = new Texture(Gdx.files.local("LWPData/" + Config.wcd.getDirectorName() + "/" + layers[size - i - 1]));
        }
        if(texture !=null) {
            for (Texture t : texture) {
                if (t != null)
                    t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            }
        }
    }
}
