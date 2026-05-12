import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App, RouterTestingModule]
    }).compileComponents();
  });

  it('should create the app and render sidebar', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('CoffeeGuin');
    expect(compiled.textContent).toContain('Pedido');
    expect(compiled.textContent).toContain('Inventario');
    expect(compiled.textContent).toContain('Reportes');
  });
});
