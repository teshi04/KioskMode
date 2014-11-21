package jp.tsur.kioskmodesample;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] apkBytes = new byte[0];
        try {
            apkBytes = IOUtils.toByteArray(getAssets().open("deviceowner-debug.apk"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        digest.update(apkBytes);

        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this);
        try {
            Properties p = new Properties();
            p.setProperty(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME, "jp.tsur.kioskmodesample");
            p.setProperty(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION, "https://www.dropbox.com/s/6lhrmhqdnyk9ixx/deviceowner-debug.apk?dl=0");
            p.setProperty(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM, Base64.encodeToString(digest.digest(), Base64.URL_SAFE));

            StringWriter stringWriter = new StringWriter();
            p.store(stringWriter, "");

            NdefMessage msg = new NdefMessage(NdefRecord.createMime(DevicePolicyManager.MIME_TYPE_PROVISIONING_NFC, stringWriter.toString().getBytes("UTF-8")));
            defaultAdapter.setNdefPushMessage(msg, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
}
