import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Auction from './auction';
import AuctionDetail from './auction-detail';
import AuctionUpdate from './auction-update';
import AuctionDeleteDialog from './auction-delete-dialog';

const AuctionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Auction />} />
    <Route path="new" element={<AuctionUpdate />} />
    <Route path=":id">
      <Route index element={<AuctionDetail />} />
      <Route path="edit" element={<AuctionUpdate />} />
      <Route path="delete" element={<AuctionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AuctionRoutes;
