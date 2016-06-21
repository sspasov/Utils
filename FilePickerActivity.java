package com.example.mypermissionsapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilePickerActivity extends AppCompatActivity
    implements AdapterView.OnItemClickListener {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    /**
     * The file path
     */
    public final static String EXTRA_FILE_PATH = "file_path";

    /**
     * Sets whether hidden files should be visible in the list or not
     */
    public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";

    /**
     * The allowed file extensions in an ArrayList of Strings
     */
    public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";

    /**
     * The initial directory which will be used if no directory has been sent with the intent
     */
    private final static String DEFAULT_INITIAL_DIRECTORY = "/";

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    protected File mDirectory;
    protected ArrayList<File> mFiles;
    protected FilePickerListAdapter mAdapter;
    protected boolean mShowHiddenFiles = false;
    protected String[] acceptedFileExtensions;

    // ---------------------------------------------------------------------------------------------
    // New intent
    // ---------------------------------------------------------------------------------------------
    public static Intent newIntent(Context context, String filePath, boolean showHiddenFiles,
        String[] fileExtension) {
        Intent intent = new Intent(context, FilePickerActivity.class);
        if (filePath != null) {
            intent.putExtra(EXTRA_FILE_PATH, filePath);
        }
        intent.putExtra(EXTRA_SHOW_HIDDEN_FILES, showHiddenFiles);
        intent.putExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS, fileExtension);
        return intent;
    }

    // ---------------------------------------------------------------------------------------------
    // Activity lifecycle
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListView listView = new ListView(this);
        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        listView.setLayoutParams(params);
        addContentView(listView, params);

        // Set the view to be shown if the list is empty
        TextView empty = createEmptyView();
        ((ViewGroup) listView.getParent()).addView(empty);
        listView.setEmptyView(empty);
        listView.setOnItemClickListener(this);

        // Initialize the ArrayList
        mFiles = new ArrayList<File>();

        // Set the ListAdapter
        mAdapter = new FilePickerListAdapter(this, mFiles);
        listView.setAdapter(mAdapter);

        // Set initial directory
        mDirectory = new File(DEFAULT_INITIAL_DIRECTORY);
        // Get intent extras
        if (getIntent().hasExtra(EXTRA_FILE_PATH)) {
            mDirectory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));
        }

        // Initialize the extensions array to allow any file extensions
        acceptedFileExtensions = new String[]{};
        if (getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
            acceptedFileExtensions =
                getIntent().getStringArrayExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);
        }

        if (getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
            mShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
        }
    }

    @Override
    protected void onResume() {
        refreshFilesList();
        super.onResume();
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private TextView createEmptyView() {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setText("No files or directories");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        return textView;
    }

    /**
     * Updates the list view to the current directory
     */
    private void refreshFilesList() {
        // Clear the files ArrayList
        mFiles.clear();

        // Set the extension file filter
        ExtensionFilenameFilter filter = new ExtensionFilenameFilter(acceptedFileExtensions);

        // Get the files in the directory
        File[] files = mDirectory.listFiles(filter);
        if (files != null && files.length > 0) {
            for (File f : files) {
                if (f.isHidden() && !mShowHiddenFiles) {
                    // Don't add the file
                    continue;
                }

                // Add the file the ArrayAdapter
                mFiles.add(f);
            }

            Collections.sort(mFiles, new FileComparator());
        }
        mAdapter.notifyDataSetChanged();
    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        if (mDirectory.getParentFile() != null) {
            // Go to parent directory
            mDirectory = mDirectory.getParentFile();
            refreshFilesList();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File newFile = (File) parent.getItemAtPosition(position);

        if (newFile.isFile()) {
            // Set result
            Intent extra = new Intent();
            extra.putExtra(EXTRA_FILE_PATH, newFile.getAbsolutePath());
            setResult(RESULT_OK, extra);
            // Finish the activity
            finish();
        } else {
            mDirectory = newFile;
            // Update the files list
            refreshFilesList();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Private classes
    // ---------------------------------------------------------------------------------------------
    private class FilePickerListAdapter extends ArrayAdapter<File> {

        private List<File> mObjects;

        public FilePickerListAdapter(Context context, List<File> objects) {
            super(context, -1);
            mObjects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewItem row;
            if (convertView == null) {
                row = new ListViewItem(getContext(), true);
            } else {
                row = (ListViewItem) convertView;
            }

            File object = mObjects.get(position);
            row.setText(object.getName());
            if (row.shouldHaveImage()) {
                if (object.isFile()) {
                    row.setImage(R.drawable.file);
                } else {
                    row.setImage(R.drawable.folder);
                }
            }
            return row;
        }
    }

    private class ListViewItem extends LinearLayout {

        private ImageView mImageView;
        private TextView mTextView;
        private boolean bHasImage = false;

        public ListViewItem(Context context, boolean withImage) {
            super(context);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            setLayoutParams(params);
            setOrientation(HORIZONTAL);
            bHasImage = withImage;
            if (bHasImage) {
                mImageView = createImageView(context);
            }
            mTextView = createTextView(context);

            addView(mImageView);
            addView(mTextView);
        }

        public void setText(String text) {
            if (mTextView != null) {
                mTextView.setText(text);
            }
        }

        public void setImage(int resourceId) {
            if (bHasImage && mImageView != null) {
                mImageView.setImageResource(resourceId);
            }
        }

        public boolean shouldHaveImage() {
            return bHasImage;
        }

        private ImageView createImageView(Context context) {
            ImageView imageView = new ImageView(context);
            int size = dpToPx(40);
            int margin = dpToPx(5);
            LayoutParams params = new LayoutParams(size, size);
            params.setMargins(margin, margin, 0, margin);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        private TextView createTextView(Context context) {
            TextView textView = new TextView(context);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(dpToPx(10), 0, 0, 0);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            textView.setSingleLine(true);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            return textView;
        }

        private int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem()
                .getDisplayMetrics().density);
        }
    }

    private class FileComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            if (f1 == f2) {
                return 0;
            }
            if (f1.isDirectory() && f2.isFile()) {
                // Show directories above files
                return -1;
            }
            if (f1.isFile() && f2.isDirectory()) {
                // Show files below directories
                return 1;
            }
            // Sort the directories alphabetically
            return f1.getName()
                .compareToIgnoreCase(f2.getName());
        }
    }

    private class ExtensionFilenameFilter implements FilenameFilter {
        private String[] mExtensions;

        public ExtensionFilenameFilter(String[] extensions) {
            super();
            mExtensions = extensions;
        }

        @Override
        public boolean accept(File dir, String filename) {
            if (new File(dir, filename).isDirectory()) {
                // Accept all directory names
                return true;
            }
            if (mExtensions != null && mExtensions.length > 0) {
                for (int i = 0; i < mExtensions.length; i++) {
                    if (filename.endsWith(mExtensions[i])) {
                        // The filename ends with the extension
                        return true;
                    }
                }
                // The filename did not match any of the extensions
                return false;
            }
            // No extensions has been set. Accept all file extensions.
            return true;
        }
    }
}
