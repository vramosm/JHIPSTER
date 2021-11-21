import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IExperiencia, Experiencia } from '../experiencia.model';
import { ExperienciaService } from '../service/experiencia.service';

@Component({
  selector: 'jhi-experiencia-update',
  templateUrl: './experiencia-update.component.html',
})
export class ExperienciaUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    titulo: [null, [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
    descripcion: [null, [Validators.minLength(5), Validators.maxLength(300)]],
    localizacion: [null, [Validators.minLength(5), Validators.maxLength(250)]],
    fecha: [null, [Validators.required]],
  });

  constructor(protected experienciaService: ExperienciaService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ experiencia }) => {
      if (experiencia.id === undefined) {
        const today = dayjs().startOf('day');
        experiencia.fecha = today;
      }

      this.updateForm(experiencia);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const experiencia = this.createFromForm();
    if (experiencia.id !== undefined) {
      this.subscribeToSaveResponse(this.experienciaService.update(experiencia));
    } else {
      this.subscribeToSaveResponse(this.experienciaService.create(experiencia));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExperiencia>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(experiencia: IExperiencia): void {
    this.editForm.patchValue({
      id: experiencia.id,
      titulo: experiencia.titulo,
      descripcion: experiencia.descripcion,
      localizacion: experiencia.localizacion,
      fecha: experiencia.fecha ? experiencia.fecha.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): IExperiencia {
    return {
      ...new Experiencia(),
      id: this.editForm.get(['id'])!.value,
      titulo: this.editForm.get(['titulo'])!.value,
      descripcion: this.editForm.get(['descripcion'])!.value,
      localizacion: this.editForm.get(['localizacion'])!.value,
      fecha: this.editForm.get(['fecha'])!.value ? dayjs(this.editForm.get(['fecha'])!.value, DATE_TIME_FORMAT) : undefined,
    };
  }
}
