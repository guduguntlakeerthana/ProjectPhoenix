import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiServiceClient } from '../../services/ai';

export interface ChatMessage {
  sender: 'user' | 'ai';
  text: string;
  simulated?: boolean;
}

@Component({
  selector: 'app-ai-assistant',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai.html',
  styleUrl: './ai.css'
})
export class AiAssistant {

  messages = signal<ChatMessage[]>([
    {
      sender: 'ai',
      text: "Hello! I am your DevFlow AI Sprint & Planning Assistant. How can I help you optimize your workflows today?"
    }
  ]);

  currentPrompt = '';
  isGenerating = signal(false);

  suggestions = [
    "Sprint breakdown for a SaaS MVP",
    "Technical doc draft for a user auth service",
    "List database tables for a blogging system",
    "Sprint planning recommendations"
  ];

  constructor(private aiClient: AiServiceClient) {}

  sendPrompt(promptText?: string): void {
    const prompt = (promptText || this.currentPrompt).trim();
    if (!prompt) return;

    // Append user message
    this.messages.update(msgs => [...msgs, { sender: 'user', text: prompt }]);
    this.currentPrompt = '';
    this.isGenerating.set(true);

    this.aiClient.askAi(prompt).subscribe({
      next: (res) => {
        this.messages.update(msgs => [...msgs, { 
          sender: 'ai', 
          text: res.response,
          simulated: res.simulated
        }]);
        this.isGenerating.set(false);
      },
      error: (err) => {
        console.error('AI assistant error', err);
        this.messages.update(msgs => [...msgs, { 
          sender: 'ai', 
          text: "I encountered an error communicating with the AI gateway. Please check your internet connection or backend configurations."
        }]);
        this.isGenerating.set(false);
      }
    });
  }
}
