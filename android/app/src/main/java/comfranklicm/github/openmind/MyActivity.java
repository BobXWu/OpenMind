package comfranklicm.github.openmind;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import comfranklicm.github.openmind.Httprequests.HttpPostImageRunnable;
import comfranklicm.github.openmind.Httprequests.HttpPostRunnable;
import comfranklicm.github.openmind.JsonParsing.LoginJsonParser;
import comfranklicm.github.openmind.JsonParsing.ViewAllProjectsJsonParser;
import comfranklicm.github.openmind.JsonParsing.ViewVoteProjectsJsonParser;
import comfranklicm.github.openmind.utils.Active;
import comfranklicm.github.openmind.utils.ActiveInfo;
import comfranklicm.github.openmind.utils.DataBaseUtil;
import comfranklicm.github.openmind.utils.MD5;
import comfranklicm.github.openmind.utils.NetUtil;
import comfranklicm.github.openmind.utils.ProjectInfo;
import comfranklicm.github.openmind.utils.User;

/**
 * Created and Modified by:LiChangMao
 * Time:2016/8/26
 */
public class MyActivity extends FragmentActivity implements OnClickListener{

    FragmentManager fManager;
    private Fragment1 fg1;
	private Fragment2 fg2;
	private Fragment3 fg3;
	private LoginFragment fg7;
    private RegisterFragment fg8;
    private SettingFragment fg9;
    private ProjectDetailFragment fg10;
    private ChildCommentListFragment fg11;
    private FileViewFragment fg12;
    private VoteProjectDetailFragment fg13;
    private ActiveDegreeFragment fg14;
    private AboutMeFragment fg15;
    private RelativeLayout course_layout;
	private RelativeLayout found_layout;
	private RelativeLayout settings_layout;
	private ImageView course_image;
	private ImageView found_image;
	private ImageView settings_image;
	private TextView course_text;
	private TextView settings_text;
	private TextView found_text;
	private int whirt = 0xFFFFFFFF;
	private int gray = 0xFF7597B3;
	private int blue =0xFF0AB2FB;
    private Handler mHandler;
    Dialog dialog;
    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final String IMGURL = Environment.getExternalStorageDirectory().toString();
    /* 照相机缓存头像名称 */
    private static final String IMAGE_FILE_NAME_TEMP = "temp_faceImage.jpg";
    /* 头像名称 */
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Fresco.initialize(this);
        User.getInstance().setMyActivity(this);
        //User.getInstance().setAllView(this.getCurrentFocus());
        User.getInstance().addAllJsonParse();
        NetUtil.getInstance().setIpAddress("139.196.15.168");
        //NetUtil.getInstance().setIpAddress("1.1.1.1");
        NetUtil.getInstance().setPort("80");
		fManager = getSupportFragmentManager();
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.card_view), iconFont);
        initdatas();
		initViews();
        if (!NetUtil.isNetworkConnectionActive(this))
        {
            Toast.makeText(this,"请检查网络连接",Toast.LENGTH_LONG).show();
        }
	}

	public void initViews()
	{
		course_image = (ImageView) findViewById(R.id.course_image);
		found_image = (ImageView) findViewById(R.id.found_image);
		settings_image = (ImageView) findViewById(R.id.setting_image);
		course_text = (TextView) findViewById(R.id.course_text);
		found_text = (TextView) findViewById(R.id.found_text);
		settings_text = (TextView) findViewById(R.id.setting_text);
		course_layout = (RelativeLayout) findViewById(R.id.course_layout);
		found_layout = (RelativeLayout) findViewById(R.id.found_layout);
		settings_layout = (RelativeLayout) findViewById(R.id.setting_layout);
		course_layout.setOnClickListener(this);
		found_layout.setOnClickListener(this);
		settings_layout.setOnClickListener(this);
        setChioceItem(0);
    }
   public void initdatas()
   {
       DataBaseUtil dataBaseUtil=DataBaseUtil.getInstance(this);
       SQLiteDatabase database=dataBaseUtil.getWritableDatabase();
       Cursor localCursor = database.rawQuery("select username,password,realname,department,signuptime from User" +
               " where id=?", new String[]{"1"});
       if(localCursor.getCount()>0)
       {
           User.getInstance().setIsLastLogin(true);
           localCursor.moveToFirst();
           int userNameColumnIndex=localCursor.getColumnIndex("username");
           User.getInstance().setUserName(localCursor.getString(userNameColumnIndex));
           int passwordColumnIndex=localCursor.getColumnIndex("password");
           User.getInstance().setPassWord(localCursor.getString(passwordColumnIndex));
           int realNameColumnIndex=localCursor.getColumnIndex("realname");
           User.getInstance().setRealName(localCursor.getString(realNameColumnIndex));
           int departmentColumnIndex=localCursor.getColumnIndex("department");
           User.getInstance().setDepartment(localCursor.getString(departmentColumnIndex));
           int signuptimeColumnIndex=localCursor.getColumnIndex("signuptime");
           User.getInstance().setRegisterTime(localCursor.getString(signuptimeColumnIndex));
           HttpPostRunnable runnable=new HttpPostRunnable();

           if (NetUtil.isNetworkConnectionActive(this))
           {
           //Toast.makeText(this,"网络连接",Toast.LENGTH_LONG).show();
           runnable.setActionId(2);
           runnable.setUsername(User.getInstance().getUserName());
           runnable.setPassword(MD5.getMD5Str(User.getInstance().getPassWord()));
           Thread t=new Thread(runnable);
           t.start();
           try{
               t.join();
           }catch (InterruptedException e)
           {
               e.printStackTrace();
           }
           //runnable.setStrResult("{\"result\":\"true\",\"realname\":\"李昌懋\",\"department\":\"软件学院\",\"signup_time\":\"2016-08-13\",\"head\":\"img/img.jpg\",\"token\":\"233\"}");
           try {
               ((LoginJsonParser) User.getInstance().baseJsonParsers.get(1)).LoginJsonParsing(runnable.getStrResult());
           }catch (Exception e)
           {
               Toast.makeText(this,"连接服务器失败",Toast.LENGTH_LONG).show();
           }
               //JsonParser.ParseJson(2, runnable.getStrResult());
           }
           else {
               Toast.makeText(this,"请检查网络连接",Toast.LENGTH_LONG).show();
           }
       }
       localCursor.close();
       if (User.getInstance().isLastLogin())
       {
           Log.d("lastlogin", "true");
           Cursor cursor = database.rawQuery("select * from ProjectInfo", null);
            while (cursor.moveToNext())
            {
                Log.d("lastlogin2", "true");
                int idColumnIndex=cursor.getColumnIndex("id");
                int nameColumnIndex=cursor.getColumnIndex("proj_name");
                int ownUserColumnIndex=cursor.getColumnIndex("own_usr");
                int ownNameColumnIndex=cursor.getColumnIndex("own_name");
                int ownHeadColumnIndex=cursor.getColumnIndex("own_head");
                int pubTimeColumnIndex=cursor.getColumnIndex("pub_time");
                int label1ColumnIndex=cursor.getColumnIndex("label1");
                int label2ColumnIndex=cursor.getColumnIndex("label2");
                int introductionColumnIndex=cursor.getColumnIndex("introduction");
                ProjectInfo projectInfo = new ProjectInfo();
                projectInfo.setProjectId(cursor.getString(idColumnIndex));
                projectInfo.setProjectName(cursor.getString(nameColumnIndex));
                projectInfo.setOwnUser(cursor.getString(ownUserColumnIndex));
                projectInfo.setOwnName(cursor.getString(ownNameColumnIndex));
                projectInfo.setOwn_head(cursor.getString(ownHeadColumnIndex));
                projectInfo.setPubTime(cursor.getString(pubTimeColumnIndex));
                projectInfo.setLabel1(cursor.getString(label1ColumnIndex));
                projectInfo.setLabel2(cursor.getString(label2ColumnIndex));
                projectInfo.setIntroduction(cursor.getString(introductionColumnIndex));
                //System.out.println(User.getInstance().owninfos.get(i).getProjectId());
                //System.out.println(User.getInstance().owninfos.get(i).getProjectName());
                User.getInstance().owninfos.add(projectInfo);
            }
           cursor.close();
           Cursor cursor1 = database.rawQuery("select * from ActiveInfo", null);
           while (cursor1.moveToNext())
           {
               Log.d("activedata", "sdaf");
               int monthColumnIndex=cursor1.getColumnIndex("month");
               int activeColumnIndex=cursor1.getColumnIndex("active");
               ActiveInfo activeInfo = new ActiveInfo();
               activeInfo.setMonth(cursor1.getString(monthColumnIndex));
               activeInfo.setActive(cursor1.getString(activeColumnIndex));
               try {
                   Log.d("active", cursor1.getString(activeColumnIndex));
                   JSONArray jsonArray = new JSONArray(cursor1.getString(activeColumnIndex));
                   List<Active>activeList=new ArrayList<Active>();
                   for (int k=0;k<jsonArray.length();k++)
                   {
                       JSONObject jsonObject=(JSONObject)jsonArray.get(k);
                       Active active=new Active();
                       active.setDay(jsonObject.getString("day"));
                       active.setDegree(jsonObject.getString("degree"));
                       activeList.add(active);
                   }
                   activeInfo.setActiveList(activeList);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               User.getInstance().ownactives.add(activeInfo);
           }
           cursor1.close();
       }
       database.close();
       if (NetUtil.isNetworkConnectionActive(this)) {
           HttpPostRunnable runnable = new HttpPostRunnable();
           runnable.setActionId(7);
           runnable.setPageSize("5");
           runnable.setTime_max("" + System.currentTimeMillis() / 1000L);
           Thread thread = new Thread(runnable);
           thread.start();
           try {
               thread.join();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           try {
               ((ViewAllProjectsJsonParser) User.getInstance().baseJsonParsers.get(6)).ViewAllProjectsJsonParsing(runnable.getStrResult());
           } catch (NullPointerException e) {
               e.printStackTrace();
           }
           HttpPostRunnable runnable1 = new HttpPostRunnable();
           runnable1.setActionId(14);
           Thread thread1 = new Thread(runnable1);
           thread1.start();
           try {
               thread1.join();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           try {
               ((ViewVoteProjectsJsonParser) User.getInstance().baseJsonParsers.get(13)).ViewVoteProjectsJsonParsing(runnable1.getStrResult());
           } catch (NullPointerException e) {
               e.printStackTrace();
           }
       }else {
           Toast.makeText(this,"网络连接失败，请检查网络",Toast.LENGTH_LONG).show();
       }
   }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       if (keyCode == KeyEvent.KEYCODE_BACK ){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.course_layout:
			setChioceItem(0);
			break;
	    case R.id.found_layout:
	    	setChioceItem(1);
	    	break;
	    case R.id.setting_layout:
	    	setChioceItem(2);
	    	break;
	    default:
			break;
		}
	}

	public void setChioceItem(int index)
	{
		final FragmentTransaction transaction = fManager.beginTransaction();
		clearChioce();
		hideFragments(transaction);
		switch (index) {
		case 0:
			course_image.setImageResource(R.drawable.ic_tabbar_found_pressed);
			course_text.setTextColor(blue);
			course_layout.setBackgroundResource(R.drawable.ic_tabbar_bg_click);
            User.getInstance().setPageNumber(0);
            if (fg1 == null || User.getInstance().index == 1) {
                User.getInstance().index = 0;
                fg1 = new Fragment1();
                transaction.add(R.id.content, fg1);
            } else {
                transaction.show(fg1);
            }
            transaction.commit();
            break;
		case 1:
			found_image.setImageResource(R.drawable.ic_tabbar_course_pressed);
			found_text.setTextColor(blue);
			found_layout.setBackgroundResource(R.drawable.ic_tabbar_bg_click);
            User.getInstance().setPageNumber(1);
            if (User.getInstance().isLastLogin()||User.getInstance().isLogin()) {
                if (fg2 == null) {
                    fg2 = new Fragment2();
                    transaction.add(R.id.content, fg2);
                } else {
                    transaction.show(fg2);
                }
                transaction.commit();
            }else {
                transactiontoLogin();
            }
            break;      
		
		 case 2:
			settings_image.setImageResource(R.drawable.ic_tabbar_settings_pressed);  
			settings_text.setTextColor(blue);
			settings_layout.setBackgroundResource(R.drawable.ic_tabbar_bg_click);
             User.getInstance().setPageNumber(2);
             fg3 = new Fragment3();
             transaction.add(R.id.content, fg3);
             transaction.commit();
            break;                 
		}

	}

	private void hideFragments(FragmentTransaction transaction) {  
        if (fg1 != null) {  
            transaction.hide(fg1);  
        }  
        if (fg2 != null) {  
            transaction.hide(fg2);  
        }  
        if (fg3 != null) {  
            transaction.hide(fg3);  
        }
		if (fg7!=null){
			transaction.hide(fg7);
		}
        if (fg8!=null){
            transaction.hide(fg8);
        }
        if (fg9!=null){
            transaction.hide(fg9);
        }
        if (fg10!=null){
            transaction.hide(fg10);
        }
        if (fg11!=null){
            transaction.hide(fg11);
        }
        if(fg12!=null){
            transaction.hide(fg12);
        }
        if (fg13 != null) {
            transaction.hide(fg13);
        }
        if (fg14 != null) {
            transaction.hide(fg14);
        }
        if (fg15 != null) {
            transaction.hide(fg15);
        }
    }

	public void clearChioce()
	{
		course_image.setImageResource(R.drawable.ic_tabbar_found_normal);
		course_layout.setBackgroundColor(whirt);
		course_text.setTextColor(gray);
		found_image.setImageResource(R.drawable.ic_tabbar_course_normal);
		found_layout.setBackgroundColor(whirt);
		found_text.setTextColor(gray);
		settings_image.setImageResource(R.drawable.ic_tabbar_settings_normal);
		settings_layout.setBackgroundColor(whirt);
		settings_text.setTextColor(gray);
        User.getInstance().currentChildComments.clear();
        User.getInstance().currentParentComments.clear();
	}
	public void transactiontoLogin()
	{
		final FragmentTransaction transaction = fManager.beginTransaction();
		hideFragments(transaction);
        fg7 = new LoginFragment();
        transaction.add(R.id.content, fg7);
        transaction.commit();
	}
    public void transactiontoRegister()
    {
        final FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        fg8 = new RegisterFragment();
        transaction.add(R.id.content, fg8);
        transaction.commit();
    }
    public void transactiontoSetting()//add by lyy 2016.8.31 切换到设置界面
    {
        final FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        fg9 = new SettingFragment();
        transaction.add(R.id.content, fg9);
        transaction.commit();
    }
    public void transactiontoProjectDetail()//add by lyy 2016.8.31 切换到项目详情界面
    {
        final FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        fg10 = new ProjectDetailFragment();
        transaction.add(R.id.content, fg10);
        transaction.commit();
    }
    public void transactiontoChildComment()
    {
        final FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        fg11 = new ChildCommentListFragment();
        transaction.add(R.id.content, fg11);
        transaction.commit();
    }
    public void transactiontoFileView()
    {
        final FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        fg12 =new FileViewFragment();
        transaction.add(R.id.content,fg12);
        transaction.commit();
    }

    public void transactiontoVoteProjectDetail() {
        final FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        fg13 = new VoteProjectDetailFragment();
        transaction.add(R.id.content, fg13);
        transaction.commit();
    }

    public void transactiontoActiveDegree() {
        final FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        fg14 = new ActiveDegreeFragment();
        User.getInstance().setActiveDegreeFragment(fg14);
        transaction.add(R.id.content, fg14);
        transaction.commit();
    }

    public void transactiontoAboutMe() {
        final FragmentTransaction transaction = fManager.beginTransaction();
        hideFragments(transaction);
        fg15 = new AboutMeFragment();
        transaction.add(R.id.content, fg15);
        transaction.commit();
    }
    public void on_click(View v) {
        switch (v.getId()) {
            case R.id.openPhones:
                openPhones();
                break;
            case R.id.cancel:
                dialog.cancel();
                break;
            default:
                break;
        }
    }
    // 打开相册
    private void openPhones() {
        Intent intentFromGallery = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentFromGallery.setType("image/*"); // 设置文件类型
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }
    public void showDialog() {
        View view1 =getLayoutInflater().inflate(R.layout.photo_choose_dialog,
                null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y =getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:// 打开相册返回
                    startPhotoZoom(data.getData());
                    break;
                case RESULT_REQUEST_CODE:// 裁剪完成,删除照相机缓存的图片
                    final File tempFile = new File(IMGURL + IMAGE_FILE_NAME_TEMP);
                    if (tempFile.exists()) {
                        new Thread() {
                            public void run() {
                                tempFile.delete();
                            }
                        }.start();
                    }
                    // //将data通过onActivityResult返回上一个界面
                    // if (data != null) {
                    // this.setResult(1, data);
                    // finish();
                    // }
                    // 保存截取后的图片
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] buffer=baos.toByteArray();
                            String data1 = Base64.encodeToString(buffer,
                                    Base64.DEFAULT);
                            //userimg.setImageBitmap(photo);
                            try {
                                File f = new File(IMGURL + "/"+IMAGE_FILE_NAME);
                                Log.d("fileurl",IMGURL + IMAGE_FILE_NAME);
                                if (!f.exists()) {
                                    f.createNewFile();
                                }
                                FileOutputStream fOut = new FileOutputStream(f);
                                photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                fOut.flush();
                                fOut.close();
                                Log.d("data1", data1);
                                HttpPostImageRunnable r=new HttpPostImageRunnable();
                                r.setData(data1);
                                Thread t=new Thread(r);
                                t.start();
                                try {
                                    t.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(User.getInstance().getUpLoadImageResult().equals("true"))
                            {
                                dialog.cancel();//关闭dialog
                                Message msg = mHandler.obtainMessage();
                                msg.what =0;
                                mHandler.sendMessage(msg);
                            }else
                            {
                                dialog.cancel();//关闭dialog
                                Toast.makeText(this, "上传失败:" + User.getInstance().getUpLoadImageError(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }
    public void setHandler(Handler handler) {
        mHandler = handler;
    }
}
