import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'findLanguageFromKey' })
export class FindLanguageFromKeyPipe implements PipeTransform {
  private languages: { [key: string]: { name: string; rtl?: boolean } } = {
    nl: { name: 'Nederlands' },
    en: { name: 'English' },
    fr: { name: 'Français' },
    de: { name: 'Deutsch' },
    el: { name: 'Ελληνικά' },
    ja: { name: '日本語' },
    // jhipster-needle-i18n-language-key-pipe - JHipster will add/remove languages in this object
  };

  transform(lang: string): string {
    return this.languages[lang].name;
  }
}
