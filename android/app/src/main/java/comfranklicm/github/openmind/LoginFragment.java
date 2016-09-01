package comfranklicm.github.openmind;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.protocol.HTTP;

import comfranklicm.github.openmind.Httprequests.HttpPostRunnable;
import comfranklicm.github.openmind.JsonParsing.JsonParser;
import comfranklicm.github.openmind.JsonParsing.ViewActiveDataJsonParser;
import comfranklicm.github.openmind.JsonParsing.ViewOwnProjectsJsonParser;
import comfranklicm.github.openmind.utils.DataBaseUtil;
import comfranklicm.github.openmind.utils.MD5;
import comfranklicm.github.openmind.utils.User;

public class LoginFragment extends Fragment {
    Button register;
    EditText userName;
    EditText passWord;
    Button login;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_layout, container,false);
        register=(Button)view.findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   MyActivity activity=(MyActivity)getActivity();
                   activity.transactiontoRegister();
            }
        });
        userName=(EditText)view.findViewById(R.id.editText);
        passWord=(EditText)view.findViewById(R.id.editText2);
        login=(Button)view.findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserNameValid(userName.getText().toString())&&isPassWordValid(passWord.getText().toString())) {
                    HttpPostRunnable runnable = new HttpPostRunnable();
                    /*runnable.setActionId(2);
                    runnable.setUsername(userName.getText().toString());
                    runnable.setPassword(MD5.getMD5Str(passWord.getText().toString()));
                    Thread t=new Thread(runnable);
                    t.start();
                    try
                    {
                        t.join();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }*/
                    runnable.setStrResult("{\"result\":\"true\",\"realname\":\"李昌懋\",\"department\":\"软件学院\",\"signup_time\":\"2016-08-13\",\"head\":\"img/img.jpg\",\"token\":\"233\"}");
                    JsonParser.ParseJson(2,runnable.getStrResult());
                    if (User.getInstance().getLoginResult().equals("true"))
                    {
                        User.getInstance().setIsLogin(true);
                        MyActivity activity=(MyActivity)getActivity();
                        User.getInstance().setUserName(userName.getText().toString());
                        User.getInstance().setPassWord(passWord.getText().toString());
                       DataBaseUtil dataBaseUtil=DataBaseUtil.getInstance(getActivity());
                        try {
                            Object[] arrayOfObject = new Object[5];
                            arrayOfObject[0] = User.getInstance().getUserName();
                            arrayOfObject[1] = User.getInstance().getPassWord();
                            arrayOfObject[2] = User.getInstance().getRealName();
                            arrayOfObject[3] = User.getInstance().getDepartment();
                            arrayOfObject[4] = User.getInstance().getRegisterTime();
                            SQLiteDatabase writedb = dataBaseUtil.getWritableDatabase();
                            Log.d("writedb", writedb.toString());
                            writedb.execSQL("insert into User(username,password,realname,department,signuptime) values(?,?,?,?,?)", arrayOfObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        HttpPostRunnable httpPostRunnable=new HttpPostRunnable();
                        httpPostRunnable.setActionId(8);
                        Thread thread=new Thread(httpPostRunnable);
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ((ViewOwnProjectsJsonParser)User.getInstance().baseJsonParsers.get(7)).ViewOwnProjectsJsonParsing(httpPostRunnable.getStrResult());
                        HttpPostRunnable httpPostRunnable1=new HttpPostRunnable();
                        httpPostRunnable1.setActionId(11);
                        httpPostRunnable1.setNum("12");
                        Time time = new Time("GMT+8");
                        String month;
                        if (time.month<10) {
                            month = "" + time.year + "0"+time.month;
                        }
                        else {
                            month=""+time.year+time.month;
                        }
                        httpPostRunnable1.setMonth(month);
                        Thread thread1=new Thread(httpPostRunnable1);
                        thread1.start();
                        try {
                            thread1.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ((ViewActiveDataJsonParser)User.getInstance().baseJsonParsers.get(10)).ViewActiveDataJsonParsing(httpPostRunnable1.getStrResult());

                        activity.setChioceItem(User.getInstance().getPageNumber());
                    }else {
                        Toast.makeText(getContext(),"登录失败:"+User.getInstance().getLoginError(),Toast.LENGTH_LONG).show();
                    }
                }else
                {
                    Toast.makeText(getContext(),"用户名密码格式错误",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }
    private boolean isUserNameValid(String username)
    {
       return username.matches("^[0-9a-zA-Z]{3,15}$");
    }
    private boolean isPassWordValid(String password)
    {
        return password.matches("^[0-9a-zA-Z]{6,16}$");
    }
}
