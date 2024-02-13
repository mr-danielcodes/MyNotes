package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.type.Date;


public class NotesDetailsActivity extends AppCompatActivity {
    EditText titleEditText,contentEditText;
    ImageView saveNoteBtn;
    TextView pageTitle,deleteNote;
    String title,content,docId;
    Boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_details);
        titleEditText = findViewById(R.id.noteTitleText);
        contentEditText = findViewById(R.id.noteContentText);
        saveNoteBtn = findViewById(R.id.saveNoteBtn);
        pageTitle = findViewById(R.id.pageTitle);
        deleteNote = findViewById(R.id.deleteNote);

        //receive the Intent
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");
        //check if the activity in edit mode or not
        if(docId!= null && !docId.isEmpty()){
            isEditMode = true;
        }
        titleEditText.setText(title);
        contentEditText.setText(content);
        if(isEditMode){
            pageTitle.setText("Edit Your Note");
            deleteNote.setVisibility(View.VISIBLE);
        }
        //click listener for the delete btn
        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNoteFromFirebase();
            }
        });


        saveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    void deleteNoteFromFirebase() {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //note deleted successful
                if(task.isSuccessful()){
                    Utility.showToast(NotesDetailsActivity.this,"Note deleted Successfully");
                    finish();
                }
                else{
                    Utility.showToast(NotesDetailsActivity.this,"Failed to delete note");
                }
            }
        });
    }
    void saveNote() {
        String noteTitle = titleEditText.getText().toString().trim();
        String noteContent = contentEditText.getText().toString().trim();

        if (noteTitle == null || noteTitle.isEmpty()) {
            titleEditText.setText("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //update the document
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else{
            //create the new document
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //note added successful
                if(task.isSuccessful()){
                    Utility.showToast(NotesDetailsActivity.this,"Note Added Successfully");
                    finish();
                }
                else{
                    Utility.showToast(NotesDetailsActivity.this,"Failed while adding note");
                }
            }
        });
    }
}