package com.mygdx.purefaithstudio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;

public class Main extends Base {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture[];
	private ParticleLayer partlay;
	private FrameBuffer fbo;
	private TextureRegion fbr;
    private float accelX=0,lastAccelX=0,thresh=0.2f,fact=0.4f,dipMul=0.0f;
    private float accelY=0,lastAccelY=0;
    //private float accelZ=0,lastAccelZ=0;
    private int size=0;
    private boolean parallax = false;
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
        resetCamera(480,800);
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
       if(!parallax) {
           setInputProcessor();
       }
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        partlay = new ParticleLayer();
        partlay.loadeffect();
		loadImageTexture();
        if (fbo == null) {
            fbo = new FrameBuffer(Format.RGBA8888, 480, 800, false);
            fbr = new TextureRegion(fbo.getColorBufferTexture());
            fbr.flip(false, true);
        }
		//batch.setShader(partlay.shaderProgram);
		// resetCamera(sW,sH);
	}
	@Override
	public void dispose() {
		batch.dispose();
        font.dispose();
		if(partlay !=null)
		partlay.dispose();
		if(fbo != null)
		fbo.dispose();
        if(texture !=null)
            for(Texture t:texture) {
                if (t != null)
                    t.dispose();
            }
        fbr.getTexture().dispose();
        texture = null;
        fbo = null;
        fbr=null;
	}

	@Override
	public void resize(int width, int height) {
        super.resize(width, height);
        dispose();
        show();
	}

	@Override
	public void render(float delta) {
		if (!(Config.listTest.equals(ParticleLayer.EveNo))) {
            Gdx.app.log("harsim","partlay reloaded");
            if(partlay!=null)
			    partlay.loadeffect();
            loadImageTexture();
		}
		delta = delta > 0.2f ? 0.2f : delta;
        if(partlay!=null)
		    partlay.update(delta);
        //accelerometer
		accelX = Gdx.input.getAccelerometerX();
        accelX = accelX * fact+ lastAccelX * (1-fact);
	    accelY = Gdx.input.getAccelerometerY() -4.5f;
        accelY = accelY * fact+ lastAccelY * (1-fact) ;
	   /* accelZ = Gdx.input.getAccelerometerZ() ;
        accelZ = accelZ * fact+ lastAccelZ * (1-fact);*/
		draw(delta); // Main draw part

        if (Math.abs(accelX - lastAccelX) > thresh) {
            lastAccelX = accelX;
        }
        if (Math.abs(accelY - lastAccelY) > thresh) {
            lastAccelY = accelY;
        }
       /* if (Math.abs(accelZ - lastAccelZ) > thresh) {
            lastAccelZ = accelZ;
        }*/
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
                    batch.draw(texture[i], -(7 * dipMul) + (accelX * dipMul), -(7 * dipMul)+ (accelY * dipMul), 480+(14*dipMul), 800+(14*dipMul));
                }
            }
        }

        if(size>0 && texture[0] != null) {

            if(parallax) {
                batch.draw(texture[0], 0, 0, 480, 800);
            }
            else {
                batch.draw(texture[0], -20+accelX * 2, 0, 520, 800);
            }
        }

        //batch.setShader(null);
        if (partlay.pe !=null) {
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
        }
        //onscreen acclero debug
        /*font.draw(batch,"roll:"+accelX,10,780);
        font.draw(batch,"pitch:"+accelY,10,760);
        //font.draw(batch,"rotation"+accelZ,10,740);*/
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
                if (com.mygdx.purefaithstudio.Config.persistent) {
					//partlay.setWind((240 - touch.x) * 0.2f);
                    if(partlay!=null)
					    partlay.setScale(0.6f);
				}
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
                if (com.mygdx.purefaithstudio.Config.persistent) {
					//partlay.setWind((240 - touch.x) * 0.2f);
                    if(partlay!=null) {
                       /* if (ParticleLayer.LWcase == 6) {
                            partlay.setPos(touch.x, touch.y, 0);
                        }*/
                        partlay.setScale(1 / 0.6f);
                    }
				}
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
        switch(Integer.parseInt(Config.listTest)){
            case 8:
                size=1;
                parallax = false;
                texture = new Texture[size];
                texture[0] = new Texture(Gdx.files.internal("data/blue2.jpg"));
                break;
            case 9:
                size=1;
                parallax = false;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/natsu.jpg"));
                break;
            case 2:
                size=1;
                parallax = false;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/blue3.png"));
                break;
            case 3:
                size=1;
                parallax = false;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/shana.png"));
            	break;
            case 4:
                size=1;
                parallax = false;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/shana2.png"));
        	    break;
            case 5:
                size=1;
                parallax = false;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/kiritoSAO.png"));
        	    break;
			case 6:
                size=1;
                parallax = false;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/yukatagirl.png"));
			    break;
			case 7:
                size=1;
                parallax = false;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/inori35.png"));
			    break;
            case 0:
                size=3;
                parallax = true;
                texture = new Texture[size];
                texture[0] = null;
                texture[1] = new Texture(Gdx.files.internal("data/butter1.png"));
                texture[2]  = new Texture(Gdx.files.internal("data/rocks.jpg"));
                break;
            case 1:
                size=4;
                parallax = true;
                texture = new Texture[size];
                texture[0] = new Texture(Gdx.files.internal("data/ironcrack.png"));
                texture[1] = new Texture(Gdx.files.internal("data/ironhand.png"));
                texture[2] = new Texture(Gdx.files.internal("data/ironbase.png"));
                texture[3]  = new Texture(Gdx.files.internal("data/city.jpg"));
                break;
            case 10:
                size=3;
                parallax = true;
                texture = new Texture[size];
                texture[0] = new Texture(Gdx.files.internal("data/ironhideN.png"));
                texture[1] = new Texture(Gdx.files.internal("data/ironhideA.png"));
                texture[2]  = new Texture(Gdx.files.internal("data/ironhideback.jpg"));
                break;
            case 11:
                size=4;
                parallax = true;
                texture = new Texture[size];
                texture[0] = new Texture(Gdx.files.internal("data/cutout0.png"));
                texture[1] = new Texture(Gdx.files.internal("data/cutout1.png"));
                //texture[2]  = new Texture(Gdx.files.internal("data/cutout2.png"));
                texture[2] = new Texture(Gdx.files.internal("data/cutout3.png"));
                texture[3] = new Texture(Gdx.files.internal("data/cutout4.png"));
                break;
            case 12:
                size=2;
                parallax = true;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/chip.png"));
                texture[1] = new Texture(Gdx.files.internal("data/chip1.png"));
                break;
            case 13:
                size=4;
                parallax = true;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/hulkbuster0.png"));
                texture[1]  = new Texture(Gdx.files.internal("data/hulkbuster2.png"));
                texture[2]  = new Texture(Gdx.files.internal("data/hulkbuster1.png"));
                texture[3] = new Texture(Gdx.files.internal("data/hulkbuster.jpg"));
                break;
            case 14:
                size=2;
                parallax = true;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/earthcrack.png"));
                texture[1]  = new Texture(Gdx.files.internal("data/earthchip.png"));
                break;
            case 15:
                size=2;
                parallax = true;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/redcrack.png"));
                texture[1]  = new Texture(Gdx.files.internal("data/redhulk.png"));
                break;
            case 16:
                size=2;
                parallax = true;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/drag1.png"));
                texture[1]  = new Texture(Gdx.files.internal("data/drag2.png"));
                break;
            case 17:
                size=4;
                parallax = true;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/naruto0.png"));
                texture[1]  = new Texture(Gdx.files.internal("data/naruto1.png"));
                texture[2]  = new Texture(Gdx.files.internal("data/naruto2.png"));
                texture[3]  = new Texture(Gdx.files.internal("data/naruto3.png"));
                break;
            default:
                size=1;
                parallax = false;
                texture = new Texture[size];
                texture[0]  = new Texture(Gdx.files.internal("data/blue2.jpg"));
        }
        if(texture !=null) {
            for (Texture t : texture) {
                if (t != null)
                    t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            }
        }
    }
}
