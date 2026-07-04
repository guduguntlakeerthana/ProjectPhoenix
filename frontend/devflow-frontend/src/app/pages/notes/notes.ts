import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NoteService, NoteRequest, NoteResponse } from '../../services/note';
import { ProjectService, ProjectResponse } from '../../services/project';

@Component({
  selector: 'app-notes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './notes.html',
  styleUrl: './notes.css'
})
export class Notes implements OnInit {

  notes = signal<NoteResponse[]>([]);
  projects = signal<ProjectResponse[]>([]);
  isLoading = signal(true);

  // Filters
  projectFilter = signal('ALL');
  searchQuery = signal('');

  // Modal signals
  isModalOpen = signal(false);
  isEditing = signal(false);
  selectedNoteId = signal<number | null>(null);

  // Form inputs
  title = signal('');
  content = signal('');
  projectId = signal<number | null>(null);
  formError = signal('');

  constructor(
    private noteService: NoteService,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    this.loadNotes();
    this.loadProjects();
  }

  loadNotes(): void {
    this.isLoading.set(true);
    this.noteService.getNotes().subscribe({
      next: (data) => {
        this.notes.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load notes', err);
        this.isLoading.set(false);
      }
    });
  }

  loadProjects(): void {
    this.projectService.getProjects('', 'ALL', 0, 100, 'title', 'asc').subscribe({
      next: (response) => {
        this.projects.set(response.content);
      },
      error: (err) => {
        console.error('Failed to load projects list', err);
      }
    });
  }

  // Reactive computed note search & filter list
  filteredNotes = computed(() => {
    let list = this.notes();

    // Text search
    const query = this.searchQuery().trim().toLowerCase();
    if (query) {
      list = list.filter(n => 
        n.title.toLowerCase().includes(query) || 
        (n.content && n.content.toLowerCase().includes(query))
      );
    }

    // Project filtering
    const pFilter = this.projectFilter();
    if (pFilter !== 'ALL') {
      const pId = parseInt(pFilter, 10);
      list = list.filter(n => n.projectId === pId);
    }

    return list;
  });

  openCreateModal(): void {
    this.isEditing.set(false);
    this.selectedNoteId.set(null);
    this.resetForm();
    this.isModalOpen.set(true);
  }

  openEditModal(note: NoteResponse): void {
    this.isEditing.set(true);
    this.selectedNoteId.set(note.id);
    this.title.set(note.title);
    this.content.set(note.content || '');
    this.projectId.set(note.projectId || null);
    this.formError.set('');
    this.isModalOpen.set(true);
  }

  closeModal(): void {
    this.isModalOpen.set(false);
    this.resetForm();
  }

  resetForm(): void {
    this.title.set('');
    this.content.set('');
    this.projectId.set(null);
    this.formError.set('');
  }

  saveNote(): void {
    if (!this.title().trim()) {
      this.formError.set('Note title is required');
      return;
    }

    const noteData: NoteRequest = {
      title: this.title().trim(),
      content: this.content().trim(),
      projectId: this.projectId()
    };

    if (this.isEditing()) {
      const id = this.selectedNoteId();
      if (id !== null) {
        this.noteService.updateNote(id, noteData).subscribe({
          next: () => {
            this.loadNotes();
            this.closeModal();
          },
          error: (err) => {
            console.error('Failed to update note', err);
            this.formError.set(err.error?.message || 'Error updating note');
          }
        });
      }
    } else {
      this.noteService.createNote(noteData).subscribe({
        next: () => {
          this.loadNotes();
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to create note', err);
          this.formError.set(err.error?.message || 'Error creating note');
        }
      });
    }
  }

  deleteNote(id: number): void {
    if (confirm('Are you sure you want to delete this note?')) {
      this.noteService.deleteNote(id).subscribe({
        next: () => {
          this.loadNotes();
        },
        error: (err) => {
          console.error('Failed to delete note', err);
          alert('Error deleting note. Please try again.');
        }
      });
    }
  }
}
