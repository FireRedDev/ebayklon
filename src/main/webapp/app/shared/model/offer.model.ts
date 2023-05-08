import { IAuction } from 'app/shared/model/auction.model';

export interface IOffer {
  id?: number;
  offerValue?: number | null;
  offerName?: IAuction | null;
}

export const defaultValue: Readonly<IOffer> = {};
