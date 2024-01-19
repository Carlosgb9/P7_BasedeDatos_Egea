package com.example.p7_basededatos;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private StudentsSQLiteHelper dbHelper;

    private EditText etIdCard, etName, etSurname;
    private Spinner spCycles;
    private RadioGroup rgCourses;
    private RadioButton rbFirst, rbSecond;
    private Button bAdd, bRemove, bUpdate, bFindDni, bFindCycle, bFindCourse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new StudentsSQLiteHelper(this);
        initViews();
        setupButtons();
        setupSpinner();
        setupListeners();
    }

    private void initViews() {
        etIdCard = findViewById(R.id.etIdCard);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        spCycles = findViewById(R.id.spCycles);
        rgCourses = findViewById(R.id.rgCourses);
        rbFirst = findViewById(R.id.rbFirst);
        rbSecond = findViewById(R.id.rbSecond);
        bAdd = findViewById(R.id.bAdd);
        bRemove = findViewById(R.id.bRemove);
        bUpdate = findViewById(R.id.bUpdate);
        bFindDni = findViewById(R.id.bFindDni);
        bFindCycle = findViewById(R.id.bFindCycle);
        bFindCourse = findViewById(R.id.bFindCourse);
    }

    private void setupButtons() {
        bAdd.setOnClickListener(v -> insertStudent());
        bRemove.setOnClickListener(v -> deleteStudent());
        bUpdate.setOnClickListener(v -> updateStudent());
        bFindDni.setOnClickListener(v -> findStudentByIdCard());
        bFindCycle.setOnClickListener(v -> findStudentsByCycle());
        bFindCourse.setOnClickListener(v -> findStudentsByCourse());
    }

    private void setupSpinner() {
        String[] cyclesArray = new String[]{"ASIX", "DAM", "DAW"};
        ArrayAdapter<String> cyclesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cyclesArray);
        cyclesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCycles.setAdapter(cyclesAdapter);
    }

    private void setupListeners() {
        spCycles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void clearData() {
        etIdCard.setText("");
        etName.setText("");
        etSurname.setText("");
    }

    private void insertStudent() {
        String idCard = etIdCard.getText().toString();
        String name = etName.getText().toString();
        String surname = etSurname.getText().toString();
        String cycle = spCycles.getSelectedItem().toString();
        String course = (rgCourses.getCheckedRadioButtonId() == R.id.rbFirst) ? getString(R.string.first) : getString(R.string.second);

        if (idCard.isEmpty()) {
            showToast(getString(R.string.id_card_is_missing));
        } else if (name.isEmpty()) {
            showToast(getString(R.string.name_is_missing));
        } else if (surname.isEmpty()) {
            showToast(getString(R.string.surname_is_missing));
        } else {
            long result = dbHelper.insertStudent(idCard, name, surname, cycle, course);

            if (result != -1) {
                showToast(getString(R.string.student_added_successfully));
                clearData();
            } else {
                showToast(getString(R.string.error_student_exists, idCard));
            }
        }
    }

    private void deleteStudent() {
        String idCard = etIdCard.getText().toString();
        if (idCard.isEmpty()) {
            showToast(getString(R.string.id_card_is_missing));
        } else {
            int result = dbHelper.deleteStudentByIdCard(idCard);

            if (result > 0) {
                showToast(getString(R.string.student_deleted_successfully));
                clearData();
            } else
                showToast(getString(R.string.error_student_not_found_delete, idCard));
        }
    }


    private void updateStudent() {
        String idCard = etIdCard.getText().toString();
        String newName = etName.getText().toString();
        String newSurname = etSurname.getText().toString();
        String newCycle = spCycles.getSelectedItem().toString();
        String newCourse = (rgCourses.getCheckedRadioButtonId() == R.id.rbFirst) ? getString(R.string.first) : getString(R.string.second);

        if (idCard.isEmpty()) {
            showToast(getString(R.string.id_card_is_missing));
        } else if (newName.isEmpty()) {
            showToast(getString(R.string.name_is_missing));
        } else if (newSurname.isEmpty()) {
            showToast(getString(R.string.surname_is_missing));
        } else {
            int result = dbHelper.updateStudent(idCard, newName, newSurname, newCycle, newCourse);

            if (result > 0) {
                showToast(getString(R.string.student_updated_successfully));
                clearData();
            } else {
                showToast(getString(R.string.error_student_not_found_update, idCard));
            }
        }
    }

    private void findStudentByIdCard() {
        String idCard = etIdCard.getText().toString();
        if (!idCard.isEmpty()) {
            Cursor cursor = dbHelper.getStudentByIdCard(idCard);

            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(StudentsDatabaseContract.StudentsTable.COLUMN_NAME);
                int surnameIndex = cursor.getColumnIndex(StudentsDatabaseContract.StudentsTable.COLUMN_SURNAME);
                int cycleIndex = cursor.getColumnIndex(StudentsDatabaseContract.StudentsTable.COLUMN_CYCLE);
                int courseIndex = cursor.getColumnIndex(StudentsDatabaseContract.StudentsTable.COLUMN_COURSE);

                String name = cursor.getString(nameIndex);
                String surname = cursor.getString(surnameIndex);
                String cycle = cursor.getString(cycleIndex);
                String course = cursor.getString(courseIndex);

                showToast(getString(R.string.student_found, name, surname));

                etName.setText(name);
                etSurname.setText(surname);


                int position;
                position = ((ArrayAdapter<String>) spCycles.getAdapter()).getPosition(cycle);
                if (position >= 0) {
                    spCycles.setSelection(position);
                }

                if (course.equals(getString(R.string.first))) {
                    rbFirst.setChecked(true);
                } else {
                    rbSecond.setChecked(true);
                }
            } else {
                showToast(getString(R.string.student_not_found,idCard));
            }
            cursor.close();
        } else {
            showToast(getString(R.string.id_card_is_missing));
        }
    }

    private void findStudentsByCycle() {
        String cycle = spCycles.getSelectedItem().toString();
        dbHelper.getStudentsByCycleAndCreateFile(cycle, this);
    }

    private void findStudentsByCourse() {
        String cycle = spCycles.getSelectedItem().toString();
        String course = (rgCourses.getCheckedRadioButtonId() == R.id.rbFirst) ? getString(R.string.first) : getString(R.string.second);
        dbHelper.getStudentsByCourseAndCreateFile(cycle, course, this);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
