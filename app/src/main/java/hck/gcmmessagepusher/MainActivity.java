package hck.gcmmessagepusher;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends AppCompatActivity {
    public final static String serverURL = "http://ddns.toraou.com:8888/gcm/gcm.php/?androidPush=true";
    Context context;
    EditText messageBox;
    Button pushButton;
    TextView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        messageBox = (EditText) findViewById(R.id.message);
        pushButton = (Button) findViewById(R.id.push);
        loading = (TextView) findViewById(R.id.loading);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void push(View view){
        String message = messageBox.getText().toString();

        pushButton.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        if (message != null && ! message.equals("")){
            pushMessageToServer(message);
        }else{
            Toast.makeText(context, "Please input the message!", Toast.LENGTH_SHORT).show();
        }
    }

    private void pushMessageToServer(String message) {
        RequestParams params = new RequestParams();
        params.put("message", message);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(serverURL, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        pushButton.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                        Toast.makeText(context,
                                "Message push successful",
                                Toast.LENGTH_SHORT).show();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        pushButton.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                        String errorMessage = "";

                        // When Http response code is '404'
                        if (statusCode == 404) {
                            errorMessage = "Requested resource not found";
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            errorMessage = "Something went wrong at server end";
                        }
                        // When Http response code other than 404, 500
                        else {
                            errorMessage = "Unexpected Error occcured! [Most common Error: Device might "
                                    + "not be connected to Internet or remote server is not up and running], check for other errors as well";
                        }
                        Toast.makeText(context,
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
