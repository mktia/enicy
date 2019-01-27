package com.mktia.www.enicy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class SayonaraDialogFragment extends DialogFragment {

    public SayonaraDialogFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("【重要なお知らせ】\n\n" +
                "フェイスブック社の個人情報流出問題を受け、子会社であるインスタグラム社も規制を強めてまいりました。\n\n" +
                "その影響で公式API提供が終了し、当アプリでは海外製の非公式APIを用いてギリギリの状態で開発しておりましたが、先日データ提供が止められました。\n\n" +
                "現在のインスタグラムは個人アカウントに関してデータを一切提供しない方針であるため、フォローチェックアプリの開発自体ができない状態となっております。\n\n" +
                "当アプリは開発、運営、お客様対応、マーケティング等全ての業務を一名で行っており、無料提供のため収益がほぼなく、現在の状態では開発続行不可能と判断致しました。\n\n" +
                "大変残念ではございますが、サポートを停止しております。\n\n" +
                "ご利用いただいていたアカウントのIDやパスワードは端末のみに保管されており、サポート停止に伴い流出の恐れはございません。\n\n" +
                "これまでご愛顧いただきありがとうございました。")
                .setPositiveButton(R.string.dialog_close_app, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyAccountsActivity.mFinishApp = true;
                        getActivity().finishAndRemoveTask();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
