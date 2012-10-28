package cn.itcast.download;

import java.io.File;

import cn.itcast.net.download.DownloadProgressListener;
import cn.itcast.net.download.FileDownloader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadActivity extends Activity {
    private ProgressBar downloadbar;
    private EditText pathText;
    private TextView resultView;
    private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				int size = msg.getData().getInt("size");
				downloadbar.setProgress(size);
				float result = (float)downloadbar.getProgress()/ (float)downloadbar.getMax();
				int p = (int)(result*100);
				resultView.setText(p+"%");
				if(downloadbar.getProgress()==downloadbar.getMax())
					Toast.makeText(DownloadActivity.this, R.string.success, 1).show();
				break;

			case -1:
				Toast.makeText(DownloadActivity.this, R.string.error, 1).show();
				break;
			}
			
		}    	
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button button = (Button)this.findViewById(R.id.button);
        downloadbar = (ProgressBar)this.findViewById(R.id.downloadbar);
        pathText = (EditText)this.findViewById(R.id.path);
        resultView = (TextView)this.findViewById(R.id.result);
        button.setOnClickListener(new View.OnClickListener() {			
		
			public void onClick(View v) {
				String path = pathText.getText().toString();
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					File dir = Environment.getExternalStorageDirectory();//�ļ�����Ŀ¼
					download(path, dir);
				}else{
					Toast.makeText(DownloadActivity.this, R.string.sdcarderror, 1).show();
				}
			}
		});
    }
    //����UI�ؼ��ĸ���ֻ�������߳�(UI�߳�)��������ڷ�UI�̸߳���UI�ؼ������µĽ��ᷴӳ����Ļ�ϣ�ĳЩ�ؼ��������
    private void download(final String path, final File dir){
    	new Thread(new Runnable() {
			
			public void run() {
				try {
					FileDownloader loader = new FileDownloader(DownloadActivity.this, path, dir, 3);
					int length = loader.getFileSize();//��ȡ�ļ��ĳ���
					downloadbar.setMax(length);
					loader.download(new DownloadProgressListener(){
						
						public void onDownloadSize(int size) {//����ʵʱ�õ��ļ����صĳ���
							Message msg = new Message();
							msg.what = 1;
							msg.getData().putInt("size", size);							
							handler.sendMessage(msg);
						}});
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = -1;
					msg.getData().putString("error", "����ʧ��");
					handler.sendMessage(msg);
				}
			}
		}).start();
    	
    }
}