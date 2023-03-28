import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBasket } from '../basket.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../basket.test-samples';

import { BasketService } from './basket.service';

const requireRestSample: IBasket = {
  ...sampleWithRequiredData,
};

describe('Basket Service', () => {
  let service: BasketService;
  let httpMock: HttpTestingController;
  let expectedResult: IBasket | IBasket[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BasketService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Basket', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const basket = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(basket).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Basket', () => {
      const basket = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(basket).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Basket', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Basket', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Basket', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBasketToCollectionIfMissing', () => {
      it('should add a Basket to an empty array', () => {
        const basket: IBasket = sampleWithRequiredData;
        expectedResult = service.addBasketToCollectionIfMissing([], basket);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(basket);
      });

      it('should not add a Basket to an array that contains it', () => {
        const basket: IBasket = sampleWithRequiredData;
        const basketCollection: IBasket[] = [
          {
            ...basket,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBasketToCollectionIfMissing(basketCollection, basket);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Basket to an array that doesn't contain it", () => {
        const basket: IBasket = sampleWithRequiredData;
        const basketCollection: IBasket[] = [sampleWithPartialData];
        expectedResult = service.addBasketToCollectionIfMissing(basketCollection, basket);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(basket);
      });

      it('should add only unique Basket to an array', () => {
        const basketArray: IBasket[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const basketCollection: IBasket[] = [sampleWithRequiredData];
        expectedResult = service.addBasketToCollectionIfMissing(basketCollection, ...basketArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const basket: IBasket = sampleWithRequiredData;
        const basket2: IBasket = sampleWithPartialData;
        expectedResult = service.addBasketToCollectionIfMissing([], basket, basket2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(basket);
        expect(expectedResult).toContain(basket2);
      });

      it('should accept null and undefined values', () => {
        const basket: IBasket = sampleWithRequiredData;
        expectedResult = service.addBasketToCollectionIfMissing([], null, basket, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(basket);
      });

      it('should return initial array if no Basket is added', () => {
        const basketCollection: IBasket[] = [sampleWithRequiredData];
        expectedResult = service.addBasketToCollectionIfMissing(basketCollection, undefined, null);
        expect(expectedResult).toEqual(basketCollection);
      });
    });

    describe('compareBasket', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBasket(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBasket(entity1, entity2);
        const compareResult2 = service.compareBasket(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBasket(entity1, entity2);
        const compareResult2 = service.compareBasket(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBasket(entity1, entity2);
        const compareResult2 = service.compareBasket(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
