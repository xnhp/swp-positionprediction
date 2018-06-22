package project.software.uni.positionprediction.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.datatype.Bird;
import project.software.uni.positionprediction.datatype.Study;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.util.Message;
import project.software.uni.positionprediction.util.PermissionManager;

public class BirdSelect extends AppCompatActivity {

    private Button buttonSettings = null;
    private Button buttonBack = null;
    private Button buttonOpenMap = null;
    private Button buttonOpenCesium = null;

    private EditText editTextSearch = null;
    private TextView editTextNavbar = null;

    private LinearLayout scrollViewLayout = null;
    private RelativeLayout background = null;

    private final static int BIRD_SELECT = 1;
    private final static  int STUDY_SELECT = 2;

    private int state;
    private int selectedStudy;

    private Intent startIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_select);

        scrollViewLayout = (LinearLayout) findViewById(R.id.birdselect_scrollview);
        editTextSearch = (EditText) findViewById(R.id.birdselect_edittext_search);
        editTextNavbar = (TextView) findViewById(R.id.navbar_text);
        buttonSettings = (Button) findViewById(R.id.navbar_button_settings);
        buttonBack = (Button) findViewById(R.id.navbar_button_back);

        background = findViewById(R.id.background);


        LayoutInflater inflater = getLayoutInflater();

        state = STUDY_SELECT;


        final BirdSelect birdSelect = this;

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(birdSelect, Settings.class);
                startActivity(buttonIntent);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state == BIRD_SELECT) switchToStudySelect();
                else finish();
            }
        });

        editTextNavbar.setText(getResources().getString(R.string.bird_select_study_select));

        /** DONT NEED THIS
        buttonOpenMap.setOnClickListener(createOpenMapClickListener(this));

        buttonOpenCesium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startIntent =  new Intent(birdSelect, Cesium.class);
                checkForPermissions();
            }
        });*/

        editTextSearch.addTextChangedListener(createSearchTextWatcher(this));

        fillStudiesList(true);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionManager.PERMISSION_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(!PermissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, this))
                    {
                        // Fine Location permissions ist still neede. Request it
                        PermissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                R.string.dialog_permission_finelocation_text,
                                PermissionManager.PERMISSION_FINE_LOCATION,
                                this);
                    }else{
                        // all permissions are granted. Start Activity
                        startActivity(startIntent);
                    }
                }

                else {
                    // permission was not granted. Display error
                    Message.disp_error(this,
                            getResources().getString(R.string.dialog_error_title),
                            getResources().getString(R.string.dialog_permissions_needed),true);
                }
                return;
            }
            case PermissionManager.PERMISSION_FINE_LOCATION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(PermissionManager.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)){
                        // all permissions are granted. Start Activity
                        startActivity(startIntent);
                    }
                }

                else {
                    // Fine Location permission was not granted. Dislay error
                    Message.disp_error(this,
                            getResources().getString(R.string.dialog_error_title),
                            getResources().getString(R.string.dialog_permissions_needed),true);
                }
                return;
        }
    }

    @Override
    public void onBackPressed() {
        switch(state){
            case STUDY_SELECT:
                this.finish();
                break;
            case BIRD_SELECT:
                switchToStudySelect();
                break;
            default:
                super.onBackPressed();
        }
    }


    /**
     * This Method requests a list of studies from the database and inserts them into the scrollView
     * @param updateDatabase if true the Data in the database is updated before filling the scrollView
     */
    private void fillStudiesList(boolean updateDatabase){

        // request studies which already are in the database
        Study studies[] = SQLDatabase.getInstance(this).getStudies();
        if (!(studies.length == 0)) fillStudiesListView(studies, true);


        if(updateDatabase) {
            final BirdSelect birdSelect = this;

            new Thread(new Runnable() {
                @Override
                public void run() {

                    // update the studies in the database
                    SQLDatabase.getInstance(birdSelect).updateStudiesSync();

                    // update the study list
                    final Study studies[] = SQLDatabase.getInstance(birdSelect).getStudies();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            fillStudiesListView(studies, true);
                        }
                    });

                }
            }).start();
        }
    }
    /**
     * This Method ads a given Array of Studies to the scrollView
     * @param studies The Array of Studies to display
     * @param showFavorites if true the favorites are displayed above the studies in the Array
     */
    private void fillStudiesListView(final Study[] studies, boolean showFavorites){

        // remove previous content from scrollView
        scrollViewLayout.removeAllViews();

        if(showFavorites) {
            // Add favorites to scrollView
            Bird favorites[] = SQLDatabase.getInstance(this).getFavorites();
            for (int i = 0; i < favorites.length; i++) {
                if (favorites[i].getNickName() == null) {
                    addBirdToList(favorites[i], favorites[i].getId() + " (" + favorites[i].getStudyId() + ")");
                } else {
                    addBirdToList(favorites[i], favorites[i].getNickName() + " (" + favorites[i].getStudyId() + ")");
                }
            }
        }

        for(int i = 0; i < studies.length; i++){
            TextView textView = new TextView(this);
            textView.setPadding(50, 50, 50, 50);
            textView.setText(studies[i].name);

            final int index = i;

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    studySelected(studies[index]);
                    state = BIRD_SELECT;
                    selectedStudy = studies[index].id;
                    editTextSearch.setText("");
                }
            });

            scrollViewLayout.addView(textView);
        }

        scrollViewLayout.invalidate();

    }

    /**
     * This Method displays a given Array of birds in the scrollView
     * @param birds The Array of Birds to display
     */
    private void fillBirdsList(final Bird birds[]){

        // remove previous content from scrollView
        scrollViewLayout.removeAllViews();

        // iterate Birds and add them to the scrollView
        for(int i = 0; i < birds.length; i++){
            if(birds[i].getNickName() == null){
                // Bird has no nickname. Show id only
                addBirdToList(birds[i], birds[i].getId() + "");
            }
            else {
                // Bird has nickname. Show nickname (id)
                addBirdToList(birds[i], birds[i].getNickName() + " (" + birds[i].getId() + ")");
            }
        }

        scrollViewLayout.invalidate();
    }
    /**
     * This Method add a Bird with a given Name to the scrollView
     * @param bird the bird to add
     * @param name the display name of the bird to add
     */
    private void addBirdToList(final Bird bird, String name){

        // Create realative Layout. This gives the ability to add buttons for favorites
        // and delete data next to the textView
        final RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);


        relativeLayout.setLayoutParams(relativeLayoutParams);

        // Create textView to show the Name
        TextView textView = new TextView(this);
        textView.setPadding(50, 50, 50, 50);

        textView.setText(name);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birdSelected(bird);
            }
        });

        final RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        textViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        textView.setLayoutParams(textViewParams);

        textView.setId(View.generateViewId());

        relativeLayout.addView(textView);


        // Create favorite Button (star)
        final Button buttonFav = new Button(this);

        final RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                dpTopx(getResources().getDimension(R.dimen.birdselect_start_size)),
                dpTopx(getResources().getDimension(R.dimen.birdselect_start_size)));

        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        buttonFav.setLayoutParams(buttonParams);

        final BirdSelect birdSelect = this;

        final View.OnClickListener notFavButtonListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                removeFromFavorites(bird, buttonFav);
            }
        };

        final View.OnClickListener favButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFavorites(bird, buttonFav);
            }
        };

        if(bird.isFavorite()){
            buttonFav.setBackground(getResources().getDrawable(R.drawable.star, null));
            buttonFav.setOnClickListener(notFavButtonListener);
        }
        else {
            buttonFav.setBackground(getResources().getDrawable(R.drawable.star_gray, null));
            buttonFav.setOnClickListener(favButtonListener);
        }

        int margin = dpTopx(getResources().getDimension(R.dimen.birdselect_star_margin));
        int margin_right = dpTopx(getResources().getDimension(R.dimen.birdselect_start_margin_right));
        buttonParams.setMargins(margin, margin, margin_right, margin);

        buttonFav.setId(View.generateViewId());

        relativeLayout.addView(buttonFav);


        // check weather there is data for the bird in the database
        if(bird.getDateLastUpdated() != null){

            // create Button to delete the data in the database
            final Button buttonDelete = new Button(this);

            buttonDelete.setId(View.generateViewId());

            RelativeLayout.LayoutParams deleteButtonParams = new RelativeLayout.LayoutParams(
                    dpTopx(getResources().getDimension(R.dimen.birdselect_start_size)),
                    dpTopx(getResources().getDimension(R.dimen.birdselect_start_size)));

            // put the button between textView and favButton
            deleteButtonParams.addRule(RelativeLayout.LEFT_OF, buttonFav.getId());
            textViewParams.addRule(RelativeLayout.LEFT_OF, buttonDelete.getId());

            deleteButtonParams.setMargins(margin, margin, 0, margin);

            buttonDelete.setLayoutParams(deleteButtonParams);

            buttonDelete.setBackground(getResources().getDrawable(R.drawable.trash, null));

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteBirdData(relativeLayout, textViewParams, buttonDelete, buttonFav, bird);
                }
            });

            relativeLayout.addView(buttonDelete);


        } else {

            // put textView next to favButton
            textViewParams.addRule(RelativeLayout.LEFT_OF, buttonFav.getId());

        }

        // add block to scrollView
        scrollViewLayout.addView(relativeLayout);
    }

    /**
     * This Method delets the data for a given Bird from the database and removes the delete
     * Button from the Bird's entry in the scrollView
     * @param layout The layout to remove the Button from
     * @param textViewParams The textView that's next to the delete Button
     * @param button The delete Button to remove
     * @param buttonFav The favorite Button right of the delete Button
     * @param bird The bird to delete the data for
     */
    private void deleteBirdData(RelativeLayout layout, RelativeLayout.LayoutParams textViewParams, Button button, Button buttonFav, Bird bird){
        // tell the textView to be next to the favButton
        textViewParams.addRule(RelativeLayout.LEFT_OF, buttonFav.getId());
        layout.removeView(button);
        // delete data of Bird from database
        SQLDatabase.getInstance(this).deleteBirdData(bird.getStudyId(), bird.getId());
    }

    /**
     * This Method add a given bird to favorites and activates the star
     * @param bird the bird to add to favorites
     * @param button the button to activate the star for
     */
    private void addToFavorites(final Bird bird, final Button button){
        SQLDatabase.getInstance(this).setFavorite(
                bird.getStudyId(),
                bird.getId(),
                true);

        button.setBackground(getResources().getDrawable(R.drawable.star, null));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromFavorites(bird, button);
            }
        });
    }
    /**
     * This Method removes a given bird from favorites and disables the star
     * @param bird the bird to add to favorites
     * @param button the button to disable the star for
     */
    private void removeFromFavorites(final Bird bird, final Button button) {
        SQLDatabase.getInstance(this).setFavorite(
                bird.getStudyId(),
                bird.getId(),
                false);

        button.setBackground(getResources().getDrawable(R.drawable.star_gray, null));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFavorites(bird, button);
            }
        });
    }

    /**
     * This Method is called when the user selects a study.
     * It switches to BIRD_SELECT mode for the given study.
     * @param study the selected study
     */
    public void studySelected(final Study study){

        editTextNavbar.setText(getResources().getString(R.string.bird_select_bird_select));

        // get birds that are already in the database
        final Bird birds[] = SQLDatabase.getInstance(this).getBirds(study.id);
        if(birds.length > 0) fillBirdsList(birds);

        final BirdSelect birdSelect = this;

        new Thread(new Runnable() {
            @Override
            public void run() {

                // update birds in the database
                final int response = SQLDatabase.getInstance(birdSelect).updateBirdsSync(study.id);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            switch (response) {
                                case -1:
                                    // unknown network error
                                    Message.disp_error(birdSelect,
                                            birdSelect.getResources().getString(R.string.dialog_error_title),
                                            birdSelect.getResources().getString(R.string.dialog_unknown_network_error),true);
                                    break;

                                case 0:
                                    // everything went ok
                                    final Bird birds[] = SQLDatabase.getInstance(birdSelect).getBirds(study.id);

                                    if (birds.length > 0) fillBirdsList(birds);
                                    else {
                                        // TODO: dispaly warning (no accessable birds for study)
                                    }
                                    break;

                                case 1:
                                    // license terms have to be accepted
                                     Message.disp_error(birdSelect,
                                            birdSelect.getResources().getString(R.string.dialog_warning_title),
                                            birdSelect.getResources().getString(R.string.dialog_accept_licence_needed),true);
                                    break;

                                case 2:
                                    // no birds available for study
                                    Message.disp_error(birdSelect,
                                            birdSelect.getResources().getString(R.string.dialog_warning_title),
                                            birdSelect.getResources().getString(R.string.dialog_no_birds_available),true);
                                    break;
                            }
                        }
                    });
            }
        }).start();
    }
    private void birdSelected(final Bird bird){

        final BirdSelect birdSelect = this;

        startIntent = new Intent(this, OSM_new.class);
        startIntent.putExtra("selectedBird", bird);
        checkForPermissions();

    }

    /**
     * This Method switches to STUDY_SELECT mode
     */
    private void switchToStudySelect(){
        final BirdSelect birdSelect = this;
        editTextNavbar.setText(getResources().getString(R.string.bird_select_study_select));
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                fillStudiesListView(SQLDatabase.getInstance(birdSelect).getStudies(), true);
            }
        });
        editTextSearch.setText("");
        state = STUDY_SELECT;
    }
    /**
     * This Method checks weather the App has the required permissions
     * before it launches the next Activity
     */
    private void checkForPermissions(){
        if (PermissionManager.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this) &&
                PermissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
            // The App already has all required permissions
            startActivity(startIntent);
            return;
        }
        if(!PermissionManager.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
            // permission for extermal storage is needed. request it
            PermissionManager.requestPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    R.string.dialog_permission_storage_text,
                    PermissionManager.PERMISSION_STORAGE,
                    this);
        }else {
            if (!PermissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
                // permission for fine location is neede. request it
                PermissionManager.requestPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        R.string.dialog_permission_finelocation_text,
                        PermissionManager.PERMISSION_FINE_LOCATION,
                        this);
            }
        }
    }


    /**
     * This Method generates the OnClickListener used to open the Map
     * @param birdSelect a reference to the current object
     * @return OnClockListener
     */
    private View.OnClickListener createOpenMapClickListener(final BirdSelect birdSelect){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startIntent = new Intent(birdSelect, OSM_new.class);
                checkForPermissions();
            }
        };
    }
    /**
     * This Method generates the TextWatcher for the searchBar
     * @param birdSelect a reference to the current object
     * @return TextWatcher
     */
    private TextWatcher createSearchTextWatcher(final BirdSelect birdSelect){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(state == STUDY_SELECT)
                    // update Studie List
                    fillStudiesListView(SQLDatabase.getInstance(birdSelect).searchStudie(
                            editTextSearch.getText().toString()), false);
                else if(state == BIRD_SELECT)
                    // update Bird List
                    fillBirdsList(SQLDatabase.getInstance(birdSelect).searchBird(
                            selectedStudy,
                            editTextSearch.getText().toString()
                    ));
            }
        };
    }

    /**
     * This Method converts a dp dimension to px dimension
     * @param dp
     * @return
     */
    private int dpTopx(float dp){
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


}
